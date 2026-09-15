package id.vanard.ayatqu.presentation.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.R
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.data.LanguagePreference
import id.vanard.ayatqu.data.PrayerTimeCache
import id.vanard.ayatqu.data.local.SurahLocalCache
import id.vanard.ayatqu.data.local.JuzLocalCache
import id.vanard.ayatqu.domain.usecase.AuthUseCase
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel
import id.vanard.ayatqu.presentation.profile.contract.ProfileEvent
import id.vanard.ayatqu.presentation.profile.contract.ProfileSideEffect
import id.vanard.ayatqu.presentation.profile.contract.ProfileState
import id.vanard.ayatqu.worker.AdhanSchedulerWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val application: Application,
    private val authUseCase: AuthUseCase,
    private val adhanPreference: AdhanPreference,
    private val languagePreference: LanguagePreference,
    private val prayerTimeCache: PrayerTimeCache,
    private val surahLocalCache: SurahLocalCache,
    private val juzLocalCache: JuzLocalCache,
) : BaseMviViewModel<ProfileState, ProfileEvent, ProfileSideEffect>(
    ProfileState(
        isLoggedIn = authUseCase.currentUser != null,
        displayName = authUseCase.currentUser?.displayName
            ?: authUseCase.currentUser?.email?.substringBefore("@")
            ?: "Guest",
        email = authUseCase.currentUser?.email.orEmpty(),
        currentLanguage = languagePreference.getCurrentLanguageCode(),
    )
) {
    init {
        viewModelScope.launch {
            combine(
                adhanPreference.notificationsEnabled,
                adhanPreference.adhanSoundType,
            ) { enabled, soundType -> enabled to soundType }
                .collect { (enabled, soundType) ->
                    setState {
                        copy(
                            notificationsEnabled = enabled,
                            adhanSoundType = soundType,
                        )
                    }
                }
        }
    }

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoginClicked -> setEffect(ProfileSideEffect.NavigateToLogin)
            ProfileEvent.SignUpClicked -> setEffect(ProfileSideEffect.NavigateToSignUp)
            is ProfileEvent.NotificationsChanged -> changeNotifications(event.enabled)
            is ProfileEvent.NotificationPermissionResult -> {
                if (event.granted) enableNotifications()
            }
            ProfileEvent.SoundClicked -> setState {
                copy(showSoundDialog = true, pendingSoundType = null, soundPreviewReady = false)
            }
            is ProfileEvent.SoundTypeChanged -> {
                if (event.type in AdhanPreference.soundTypes && uiState.value.showSoundDialog) {
                    setState { copy(pendingSoundType = event.type, soundPreviewReady = false) }
                    setEffect(ProfileSideEffect.PreviewSound(event.type))
                }
            }
            is ProfileEvent.SoundPreviewStarted -> setState {
                if (showSoundDialog && pendingSoundType == event.type) copy(soundPreviewReady = true) else this
            }
            is ProfileEvent.SoundPreviewFailed -> {
                setState { if (pendingSoundType == event.type) copy(soundPreviewReady = false) else this }
                setEffect(ProfileSideEffect.ShowMessage(application.getString(R.string.sound_preview_failed)))
            }
            ProfileEvent.SoundConfirmed -> {
                val state = uiState.value
                if (state.showSoundDialog && state.soundPreviewReady) {
                    state.pendingSoundType?.let { type ->
                        viewModelScope.launch { adhanPreference.setAdhanSoundType(type) }
                    }
                    dismissSound()
                }
            }
            ProfileEvent.SoundDismissed -> dismissSound()
            is ProfileEvent.PermissionsRefreshed -> setState { copy(remindersReady = event.ready) }
            ProfileEvent.ReminderSetupClicked -> setState { copy(showReminderSetupDialog = true) }
            ProfileEvent.ReminderSetupConfirmed -> {
                setState { copy(showReminderSetupDialog = false) }
                setEffect(ProfileSideEffect.EnableNotifications)
            }
            ProfileEvent.OpenAppSettingsClicked -> setEffect(ProfileSideEffect.OpenAppSettings)
            ProfileEvent.LanguageClicked -> setState { copy(showLanguageDialog = true) }
            is ProfileEvent.LanguageSelected -> selectLanguage(event.code)
            ProfileEvent.LogoutClicked -> setState { copy(showLogoutDialog = true) }
            ProfileEvent.LogoutConfirmed -> logout()
            ProfileEvent.ClearCacheClicked -> setState { copy(showClearCacheDialog = true) }
            ProfileEvent.ClearCacheConfirmed -> clearCache()
            ProfileEvent.DialogDismissed -> setState {
                copy(
                    showLogoutDialog = false,
                    showClearCacheDialog = false,
                    showLanguageDialog = false,
                    showReminderSetupDialog = false,
                )
            }
            ProfileEvent.RateAppClicked -> setEffect(ProfileSideEffect.ShowMessage("Rating is not available yet"))
            ProfileEvent.AboutClicked -> setEffect(ProfileSideEffect.ShowMessage("AyatQu"))
        }
    }

    private fun changeNotifications(enabled: Boolean) {
        if (enabled) {
            setState { copy(showReminderSetupDialog = true) }
        } else {
            viewModelScope.launch {
                adhanPreference.setNotificationsEnabled(false)
                AdhanSchedulerWorker.cancel(application)
            }
        }
    }

    private fun enableNotifications() {
        viewModelScope.launch {
            adhanPreference.setNotificationsEnabled(true)
            AdhanSchedulerWorker.runNow(application)
        }
    }

    private fun dismissSound() {
        setState { copy(showSoundDialog = false, pendingSoundType = null, soundPreviewReady = false) }
        setEffect(ProfileSideEffect.StopSoundPreview)
    }

    private fun selectLanguage(code: String) {
        val languageCode = LanguagePreference.normalizeLanguageCode(code)
        setState { copy(showLanguageDialog = false, currentLanguage = languageCode) }
        setEffect(ProfileSideEffect.ApplyLanguage(languageCode))
    }

    private fun logout() {
        setState { copy(showLogoutDialog = false) }
        viewModelScope.launch {
            authUseCase.signOut()
            setEffect(ProfileSideEffect.NavigateToLanding)
        }
    }

    private fun clearCache() {
        setState { copy(showClearCacheDialog = false) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                prayerTimeCache.clearCache()
                surahLocalCache.clear()
                juzLocalCache.clear()
            }
            setEffect(ProfileSideEffect.ShowMessage(application.getString(R.string.cache_cleared)))
        }
    }

}
