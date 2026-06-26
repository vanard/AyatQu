package id.vanard.ayatqu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.data.OnboardingPreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val pref: OnboardingPreference) : ViewModel() {

    val isOnboardingDone: StateFlow<Boolean?> = pref.isOnboardingDone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun completeOnboarding() {
        viewModelScope.launch { pref.setOnboardingDone() }
    }
}
