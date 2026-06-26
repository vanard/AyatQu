# Architecture

## Pattern: MVVM + Clean Architecture

```
UI (Composables)
    ↓
ViewModel (androidx.lifecycle)
    ↓
UseCase / Interactor
    ↓
Repository (interface)
    ↓
Data Source (remote / local)
```

## Layer Rules
- **UI**: Only rendering and user events. No business logic.
- **ViewModel**: Holds UI state (`StateFlow`). Calls UseCases. No Android framework imports except lifecycle.
- **UseCase**: Single-responsibility. One public `operator fun invoke(...)`.
- **Repository**: Interface defined in domain layer; implementation in data layer.
- **Data Source**: Remote (API client) or local (database/prefs). Never accessed directly from ViewModel.

## State Management
- Expose UI state as a single sealed class / data class via `StateFlow<UiState>`
- Use `collectAsStateWithLifecycle()` in Composables — never `collectAsState()` directly
- Side effects (navigation, snackbar) via `SharedFlow` / Compose `LaunchedEffect`

## Module Structure (future-ready)
```
app/
  ui/          — Composables, screens, navigation
  viewmodel/   — ViewModels
  domain/      — UseCases, domain models, repository interfaces
  data/        — Repository implementations, remote/local data sources
  di/          — Koin modules (AppModule.kt)
  worker/      — WorkManager workers (e.g. QuranDownloadWorker)
```

## Dependency Injection (Koin 4.0.4)
- Initialize in `AyatQuApp : Application` via `startKoin { androidContext(); workManagerFactory(); modules(appModule) }`
- Define all bindings in `di/AppModule.kt`
- Workers registered with `workerOf(::WorkerClass)` — do NOT use `WorkerFactory` manually
- Inject into Composables via `koinViewModel()` / `get()`

## Background Work (WorkManager 2.10.1)
- Use `CoroutineWorker` for all background tasks
- Enqueue with unique work names (`WorkManager.enqueueUniqueWork`) to prevent duplicates
- Report progress via `setProgress()` and observe with `WorkInfo` in ViewModel
