package id.vanard.ayatqu.presentation.auth.landing.contract

sealed interface LandingEvent {
    data object LoginClicked : LandingEvent
    data object SignUpClicked : LandingEvent
    data object SkipClicked : LandingEvent
}

typealias OnLandingEvent = (LandingEvent) -> Unit
