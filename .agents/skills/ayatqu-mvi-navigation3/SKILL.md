---
name: ayatqu-mvi-navigation3
description: Create or refactor AyatQu screens and navigation using the project's MVI contracts, small multi-module boundaries, Router/Screen split, Koin wiring, typed AndroidX Navigation 3 routes, direction commands, entry providers, and multi-stack bottom navigation. Use for screen, ViewModel, route, navigation, module-boundary, or bottom-tab work in AyatQu.
---

# AyatQu MVI and Navigation 3

Use this pattern for new features and for changes to existing screens or navigation flows.

This pattern is adapted from MobileSATSO-v2. Use AyatQu names, dependencies, design system, domain models, and package root (`id.vanard.ayatqu`); do not copy MobileSATSO-specific APIs or base classes.

## Module boundaries

AyatQu is a small multi-module project. Split reusable infrastructure into focused core modules and keep all feature-specific code in `:app`.

Use these modules:

- `:app` owns feature presentation, MVI contracts and ViewModels, domain and data layers, workers, Koin bindings, feature routes and directions, entry providers, and the app navigation root.
- `:core:ui` owns the reusable design system: theme, typography, fonts, generic components, test-tag helpers, and local icons shared across features.
- `:core:navigation` owns generic Navigation 3 infrastructure: `AppNavigationCommand`, `NavOptions`, `NavigationManager`, and reusable multi-stack state.

Dependency direction is `:app` to core modules. A core module must not depend on `:app`, a feature route, a feature model, or another app-specific type. Keep Koin bindings in `:app`; the core modules should expose implementations that `AppModule.kt` can bind.

Do not create a Gradle module per feature. Do not create separate data or domain modules for this project size. Avoid a broad `:core:common` module; keep a generic helper in `:app` until several coherent, app-independent files justify a focused module.

Move a file to a core module only when all of these are true:

- it has a reusable responsibility rather than feature behavior;
- at least two feature areas or modules need it;
- its public API does not expose app or feature types;
- moving it creates a clear dependency boundary rather than another forwarding layer.

## Target layout

Organize new or migrated code with Gradle modules for shared infrastructure and packages for features:

```text
:app
└── app/src/main/java/id/vanard/ayatqu/
    ├── data/<feature>/...
    ├── domain/<feature>/...
    ├── di/AppModule.kt
    ├── navigation/
    │   ├── directions/<Feature>Direction.kt
    │   └── routes/<Feature>Route.kt
    └── presentation/
        ├── common/viewmodel/BaseMviViewModel.kt
        ├── root/navigation/
        │   ├── NavigationEntryProvider.kt
        │   └── NavigationRoot.kt
        └── <feature>/
            ├── navigation/<Feature>NavigationEntryProvider.kt
            └── <screen>/
                ├── contract/
                │   ├── <Screen>Event.kt
                │   ├── <Screen>State.kt
                │   └── <Screen>SideEffect.kt
                ├── <Screen>Router.kt
                ├── <Screen>Screen.kt
                ├── <Screen>TestTag.kt
                └── <Screen>ViewModel.kt

:core:ui
└── core/ui/src/main/java/id/vanard/ayatqu/core/ui/
    ├── component/...
    ├── icon/...
    └── theme/...

:core:navigation
└── core/navigation/src/main/java/id/vanard/ayatqu/core/navigation/
    ├── NavigationCommand.kt
    ├── NavigationManager.kt
    └── NavigationState.kt
```

Create data sources, repositories, use cases, interactors, and mappers only when the feature needs them. Keep repository interfaces in the domain layer and implementations in the data layer, consistent with AyatQu's existing Clean Architecture boundaries.

## MVI contract

Give each screen one explicit contract split across three files:

```kotlin
sealed interface QuranEvent {
    data object InitialData : QuranEvent
    data class QueryChanged(val query: String) : QuranEvent
    data class SurahClicked(val surahNumber: Int) : QuranEvent
}

typealias OnQuranEvent = (QuranEvent) -> Unit
```

```kotlin
data class QuranState(
    val isLoading: Boolean = false,
    val query: String = "",
    val surahs: List<Surah> = emptyList(),
    val errorMessage: String? = null,
)
```

```kotlin
sealed interface QuranSideEffect {
    data class NavigateToSurah(val surahNumber: Int) : QuranSideEffect
    data class ShowError(val message: String) : QuranSideEffect
}
```

Apply these boundaries:

- `State` is the complete persistent UI snapshot and is immutable.
- `Event` represents user actions or lifecycle inputs received by the ViewModel.
- `SideEffect` represents one-time work such as navigation, a snackbar, a toast, permission launch, or opening an external surface. Do not store one-time actions in `State`.
- Use UI models in the presentation layer when domain models do not match rendering needs. Perform mapping outside the composable.
- Do not expose multiple public intent methods from a migrated ViewModel. Route UI actions through `onEvent`.

Use a shared MVI base ViewModel equivalent to this project-adapted MobileSATSO pattern:

```kotlin
abstract class BaseMviViewModel<S, E, F>(initialState: S) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<F>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val uiEffect: SharedFlow<F> = _uiEffect.asSharedFlow()

    abstract fun onEvent(event: E)

    protected fun setState(reduce: S.() -> S) {
        _uiState.update { it.reduce() }
    }

    protected fun setEffect(effect: F) {
        _uiEffect.tryEmit(effect)
    }
}
```

If network awareness remains a cross-cutting need, compose it into or extend the shared MVI base without duplicating state/effect plumbing in each feature.

## Router and Screen

The Router is the stateful boundary:

```kotlin
@Composable
fun QuranRouter(
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is QuranSideEffect.NavigateToSurah -> {
                    navigationManager.navigate(QuranDirection.detail(effect.surahNumber))
                }
                is QuranSideEffect.ShowError -> {
                    // Show the project-standard transient UI.
                }
            }
        }
    }

    QuranScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
```

The Screen is stateless and previewable:

```kotlin
@Composable
fun QuranScreen(
    state: QuranState,
    onEvent: OnQuranEvent,
    modifier: Modifier = Modifier,
) {
    // Render state and translate UI callbacks to events.
}
```

Keep Koin lookup, lifecycle collection, side-effect collection, permission launchers, and navigation in the Router. The Screen receives plain state and callbacks. Do not inject a ViewModel or navigation object into the Screen.

## Typed Navigation 3

### Routes

Define feature-owned typed routes. Every route implements `NavKey` and is serializable:

```kotlin
@Serializable
sealed interface QuranRoute : NavKey {
    @Serializable
    data object Root : QuranRoute

    @Serializable
    data class Detail(
        val surahNumber: Int,
        val ayahNumber: Int? = null,
    ) : QuranRoute
}
```

Prefer small stable primitives or serializable IDs as route arguments. Load large or mutable objects from a repository using the ID.

### Commands and directions

Keep navigation mechanics behind commands:

```kotlin
sealed interface AppNavigationCommand {
    data class Navigate(
        val route: NavKey,
        val options: NavOptions = NavOptions(),
    ) : AppNavigationCommand

    data class PopBack(val result: Any? = null) : AppNavigationCommand

    data class PopBackTo(
        val route: NavKey,
        val inclusive: Boolean = false,
    ) : AppNavigationCommand
}
```

Expose feature destinations through a direction object:

```kotlin
object QuranDirection {
    val root = AppNavigationCommand.Navigate(QuranRoute.Root)
    val back = AppNavigationCommand.PopBack()

    fun detail(surahNumber: Int, ayahNumber: Int? = null) =
        AppNavigationCommand.Navigate(
            QuranRoute.Detail(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
            )
        )
}
```

Define `NavigationManager` and its implementation in `:core:navigation`, then bind the implementation as a Koin singleton in `:app`. It exposes a `SharedFlow<AppNavigationCommand>`. Routers send direction commands to it; the root navigation host is the single consumer that mutates the Navigation 3 back stack. Avoid direct back-stack mutation from ViewModels and Screens.

### Entry providers

Each feature supplies an `EntryProviderScope<NavKey>` extension:

```kotlin
fun EntryProviderScope<NavKey>.quran() {
    entry<QuranRoute.Root> {
        QuranRouter()
    }
    entry<QuranRoute.Detail> { route ->
        DetailSurahRouter(
            surahNumber = route.surahNumber,
            ayahNumber = route.ayahNumber,
        )
    }
}
```

Append it to the central provider without deleting existing entries:

```kotlin
val ayatQuEntryProvider = entryProvider {
    onboarding()
    auth()
    home()
    quran()
    profile()
}
```

Use `NavDisplay` in the root and include `rememberSaveableStateHolderNavEntryDecorator()` and `rememberViewModelStoreNavEntryDecorator()` so entry state and ViewModels follow Navigation 3 entries.

### Bottom tabs

Retain AyatQu's custom bottom bar. Model Home, Quran, and Profile roots as typed top-level `NavKey` values. Use a Navigation 3 stack controller that:

- maintains a separate back stack for each top-level tab;
- exposes the active tab's stack to `NavDisplay`;
- switches tabs through typed keys;
- prevents duplicate consecutive destinations;
- returns to the previous entry on back and falls back to Home from another tab's root;
- deliberately defines reselection behavior, normally resetting that tab to its root;
- saves restorable tab/back-stack state where Navigation 3 supports it.

Do not use a nested Navigation 2 `NavHost`, string routes, route string interpolation, or a screen-local `rememberSaveable` enum as the source of truth for a migrated tab flow.

## Dependency injection and registration

Register the shared navigation manager and every new ViewModel in `di/AppModule.kt`. Continue using `singleOf`, `viewModelOf`, and `bind` where they make the type relationship clear. Preserve all existing bindings and entry-provider calls when adding a feature. Do not create per-feature Koin or Gradle modules for the current project size.

## Maintenance rules

- Do not add Navigation 2 `NavHost`, `NavController`, string route constants, or screen-local bottom-tab state.
- Implement a complete navigation flow boundary: route, direction, entry provider, Router, Screen callback, ViewModel event, and side effect.
- Do not mix direct navigation callbacks and command-driven Navigation 3 within the same screen.
- Preserve the custom bottom-bar design, local Phosphor icons, authentication behavior, deep-link intent, and saved screen state while changing navigation mechanics.

## Verification

After implementation, run the narrowest relevant test, then `./gradlew assembleDebug`. Add unit tests for ViewModel event-to-state and event-to-effect behavior when logic is non-trivial. For navigation, verify typed argument delivery, back behavior, tab switching/reselection, and state restoration when the changed flow depends on them.
