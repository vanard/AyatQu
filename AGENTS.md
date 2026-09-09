# AyatQu

Quran & prayer-times Android app. Kotlin, Jetpack Compose, small multi-module project.

## Build & Run

```bash
./gradlew assembleDebug          # build debug APK
./gradlew installDebug           # build + install on connected device
./gradlew testDebugUnitTest      # unit tests
./gradlew connectedDebugAndroidTest  # instrumented tests (device required)
```

No separate lint or typecheck step — Gradle compile catches both.

## Modules

- `:app` — all feature-specific presentation, domain, data, workers, and application DI. Keep features here and organize them with packages; do not create one Gradle module per feature.
- `:core:ui` — reusable design system: theme, typography, fonts, generic UI components, and shared local icons. It must not contain feature screens or depend on `:app`.
- `:core:navigation` — generic Navigation 3 commands, navigation manager, options, and reusable multi-stack state. Feature routes, directions, entry providers, and the app navigation root remain in `:app`.
- `build-logic/convention` — Gradle convention plugins (`ayatqu.android.application`, `ayatqu.android.library`, `ayatqu.android.compose`).compileSdk=37, minSdk=26, JVM 11.

Keep the module graph simple: `:app` may depend on `:core:ui` and `:core:navigation`; core modules must never depend on `:app` or on feature types. Add another `:core:*` module only when it has one coherent reusable responsibility, is used by multiple feature areas or modules, and can expose an app-independent API. Do not add `:feature:*`, `:data:*`, `:domain:*`, or a catch-all `:core:common` module for the current project size.

## Current Architecture

```
app/src/main/java/id/vanard/ayatqu/
├── di/AppModule.kt          # Koin module — wire everything here
├── domain/                  # models, repository interfaces, use cases
├── data/                    # Retrofit clients, repository impls, caches
├── navigation/
│   ├── routes/              # typed serializable NavKey routes
│   └── directions/          # typed navigation commands per feature
├── presentation/
│   ├── common/              # app-level shared UI and MVI base
│   ├── root/navigation/     # NavDisplay, entry provider, custom bottom bar
│   └── <feature>/           # contract, Router, Screen, ViewModel, entries
└── worker/                  # QuranDownloadWorker

core/
├── navigation/              # generic Navigation 3 infrastructure
└── ui/                      # theme, fonts, and locally-defined shared icons
```

**DI**: Koin. All bindings live in `di/AppModule.kt`. New repo/VM → add it there.

**Navigation**: AndroidX Navigation 3 with typed `NavKey` routes, direction commands, feature entry providers, and retained stacks for Home, Quran, and Profile. Keep all navigation mutations in the root consumer of `NavigationManager.commands`.

**Networking**: Retrofit 3 + OkHttp 5. Two clients: `QuranRetrofitClient` (UmmahAPI) and `PrayerTimeRetrofitClient`. API key injected via header interceptor from `BuildConfig.QURAN_API_KEY`.

## Required Architecture: MVI + Navigation 3

All new features and any screen/navigation flow being substantially refactored must follow the repository-local [AyatQu MVI + Navigation 3 skill](.agents/skills/ayatqu-mvi-navigation3/SKILL.md). Read that skill before creating or changing a screen, ViewModel, route, navigation command, entry provider, or bottom-tab behavior.

The target pattern is adapted from `MobileSATSO-v2`:

- Presentation uses MVI with a separate immutable `State`, sealed `Event`, and sealed `SideEffect` for each screen.
- A ViewModel exposes `StateFlow<State>`, accepts every UI action through one `onEvent(Event)` entry point, and emits one-shot work through `SharedFlow<SideEffect>`.
- Split each screen into a stateful `*Router` and a stateless `*Screen`. The Router obtains the Koin ViewModel, collects state with lifecycle awareness, handles side effects, and performs navigation. The Screen receives state and an event callback and must not know about Koin, a navigation controller, or navigation routes.
- Navigation uses AndroidX Navigation 3. Routes are typed, `@Serializable` implementations of `NavKey`; string route constants, `NavController`, Navigation 2 `NavHost`, and route-string argument parsing are legacy-only.
- Each feature owns a `*Route`, `*Direction`, and `*NavigationEntryProvider` in `:app`. Register feature entries in the root `entryProvider` and preserve every existing entry when adding another.
- Navigation is issued as commands through the app navigation manager. ViewModels emit navigation side effects; Routers translate them into typed direction commands.
- Keep AyatQu's custom bottom bar. Back-stack and tab switching must be driven by Navigation 3 with a separate saved stack per top-level tab, following the MobileSATSO-v2 multi-stack approach. Do not add a nested Navigation 2 host or keep tab selection as screen-local `rememberSaveable` state after that flow is migrated.
- Wire every new ViewModel, repository, use case, navigation manager, and related implementation in `di/AppModule.kt` unless the project is deliberately split into feature Koin modules later.

Do not reintroduce string routes, Navigation 2, direct `NavController` use, or screen-owned navigation state.

## Environment Setup

Copy `local.properties.example` → `local.properties` (gitignored) and fill:

```
QURAN_API_KEY=<your key from ummahapi.com>
QURAN_BASE_URL=https://ummahapi.com/api/
GOOGLE_WEB_CLIENT_ID=<for Google Sign-In>
```

`app/google-services.json` is also required (gitignored) — get from Firebase console.

## Gotchas

- **PhosphorIcons are local** — shared vectors live in `:core:ui` under `core/ui/.../icon`; keep feature-specific artwork in `:app`.
- **No 3rd-party icon lib** — comment in `app/build.gradle.kts:44` documents this.
- **WorkManager init removed from manifest** — custom init via Koin `workManagerFactory()` in `AyatQuApp.kt`. Do not re-add the default initializer.
- **Chucker** — HTTP inspector in debug builds only. `debugImplementation` / `releaseImplementation` split in `app/build.gradle.kts`.
- **Splash screen** — uses `core-splashscreen` with a custom theme (`Theme.AyatQu.SplashScreen`).
- **Bottom navigation** — keep the custom bottom-bar composable and use Navigation 3 multi-stack state for tab switching, restoration, and back behavior.
