# Coding Rules

## Stack
- Language: Kotlin
- UI: Jetpack Compose + Material3
- Min SDK: 26, Target SDK: 36
- Package: `id.vanard.ayatqu`

## Code Style
- Follow official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `@Composable` functions for all UI — no XML layouts
- Prefer `val` over `var`; use immutable state where possible
- Use `StateFlow` / `collectAsStateWithLifecycle` for reactive state in ViewModels

## Dependencies
- Only add dependencies via `gradle/libs.versions.toml` (version catalog)
- Pin exact versions — no open ranges
- Use Compose BOM to manage Compose library versions

## Testing
- Unit tests in `app/src/test/`, instrumented/UI tests in `app/src/androidTest/`
- Use JUnit4 for unit tests; Compose UI test rules for UI tests

## General
- Do not commit `local.properties` or any file containing API keys/secrets
- Keep `build.gradle.kts` clean — no hardcoded version strings outside the version catalog
