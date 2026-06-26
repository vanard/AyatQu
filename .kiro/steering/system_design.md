# System Design

## Navigation
- Use Jetpack Navigation Compose (`NavHost` / `NavController`)
- Define routes as a sealed class or `object` with `const val` route strings
- Pass only primitive IDs between screens — load full data in destination ViewModel

## UI Design System
- Use Material3 components exclusively (`androidx.compose.material3`)
- Define app colors/typography in a central `Theme.kt` — no hardcoded colors in Composables
- Support dynamic color (Material You) where applicable
- Use `MaterialTheme.colorScheme` / `MaterialTheme.typography` tokens

## Data & Persistence
- Remote: Use Retrofit + OkHttp for API calls; define API interfaces
- Local: Use Room for structured data; DataStore for key-value preferences
- All I/O operations run on `Dispatchers.IO`; never block the main thread

## Error Handling
- Wrap results in `Result<T>` or a sealed `Resource<T>` (Loading / Success / Error)
- Never swallow exceptions silently — log or surface to UI state
- Show user-facing errors via UI state, not crashes

## Performance
- Use `LazyColumn` / `LazyRow` for lists — never `Column` with `forEach` for dynamic lists
- Avoid recomposition triggers: use `remember`, `derivedStateOf`, stable data classes
- Minimize work in `@Composable` functions; defer heavy computation to ViewModel
