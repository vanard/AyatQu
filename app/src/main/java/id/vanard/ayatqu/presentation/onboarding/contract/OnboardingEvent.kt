package id.vanard.ayatqu.presentation.onboarding.contract

sealed interface OnboardingEvent {
    data class PageChanged(val page: Int) : OnboardingEvent
    data object BackClicked : OnboardingEvent
    data object NextClicked : OnboardingEvent
    data object SkipClicked : OnboardingEvent
}

typealias OnOnboardingEvent = (OnboardingEvent) -> Unit
