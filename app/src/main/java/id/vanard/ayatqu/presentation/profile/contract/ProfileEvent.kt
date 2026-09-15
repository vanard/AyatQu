package id.vanard.ayatqu.presentation.profile.contract

sealed interface ProfileEvent {
    data object LoginClicked : ProfileEvent
    data object SignUpClicked : ProfileEvent
    data class NotificationsChanged(val enabled: Boolean) : ProfileEvent
    data class NotificationPermissionResult(val granted: Boolean) : ProfileEvent
    data class SoundTypeChanged(val type: String) : ProfileEvent
    data object SoundClicked : ProfileEvent
    data class SoundPreviewStarted(val type: String) : ProfileEvent
    data class SoundPreviewFailed(val type: String) : ProfileEvent
    data object SoundConfirmed : ProfileEvent
    data object SoundDismissed : ProfileEvent
    data class PermissionsRefreshed(val ready: Boolean) : ProfileEvent
    data object ReminderSetupClicked : ProfileEvent
    data object ReminderSetupConfirmed : ProfileEvent
    data object OpenAppSettingsClicked : ProfileEvent
    data object LanguageClicked : ProfileEvent
    data class LanguageSelected(val code: String) : ProfileEvent
    data object LogoutClicked : ProfileEvent
    data object LogoutConfirmed : ProfileEvent
    data object ClearCacheClicked : ProfileEvent
    data object ClearCacheConfirmed : ProfileEvent
    data object DialogDismissed : ProfileEvent
    data object RateAppClicked : ProfileEvent
    data object AboutClicked : ProfileEvent
}

typealias OnProfileEvent = (ProfileEvent) -> Unit
