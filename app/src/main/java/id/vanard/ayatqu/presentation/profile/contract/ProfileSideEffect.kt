package id.vanard.ayatqu.presentation.profile.contract

sealed interface ProfileSideEffect {
    data object NavigateToLogin : ProfileSideEffect
    data object NavigateToSignUp : ProfileSideEffect
    data object NavigateToLanding : ProfileSideEffect
    data object EnableNotifications : ProfileSideEffect
    data class ApplyLanguage(val languageCode: String) : ProfileSideEffect
    data class ShowMessage(val message: String) : ProfileSideEffect
}
