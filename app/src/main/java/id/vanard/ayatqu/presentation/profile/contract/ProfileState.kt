package id.vanard.ayatqu.presentation.profile.contract

import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.data.LanguagePreference

data class ProfileState(
    val isLoggedIn: Boolean = false,
    val displayName: String = "Guest",
    val email: String = "",
    val notificationsEnabled: Boolean = false,
    val adhanSoundType: String = AdhanPreference.SOUND_TYPE_DEFAULT,
    val currentLanguage: String = LanguagePreference.LANGUAGE_ENGLISH,
    val showLogoutDialog: Boolean = false,
    val showClearCacheDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val showSoundDialog: Boolean = false,
    val pendingSoundType: String? = null,
    val soundPreviewReady: Boolean = false,
    val remindersReady: Boolean = false,
    val showReminderSetupDialog: Boolean = false,
)
