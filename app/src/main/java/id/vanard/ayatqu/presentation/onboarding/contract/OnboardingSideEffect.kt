package id.vanard.ayatqu.presentation.onboarding.contract

sealed interface OnboardingSideEffect {
    data object NavigateToLanding : OnboardingSideEffect
}
