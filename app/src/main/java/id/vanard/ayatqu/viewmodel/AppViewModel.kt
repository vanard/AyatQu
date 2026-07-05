package id.vanard.ayatqu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.data.OnboardingPreference
import id.vanard.ayatqu.domain.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Represents the startup destination decision
sealed class StartDestination {
    data object Loading : StartDestination()
    data object Onboarding : StartDestination()
    data object Landing : StartDestination()
    data object Home : StartDestination()
}

class AppViewModel(
    private val pref: OnboardingPreference,
    private val authRepository: AuthRepository,
) : ViewModel() {

    // Wrap currentUser check in a flow so it can be combined
    private val isLoggedIn = flow { emit(authRepository.currentUser != null) }

    val startDestination: StateFlow<StartDestination> = combine(
        pref.isOnboardingDone,
        isLoggedIn
    ) { onboardingDone, loggedIn ->
        when {
            !onboardingDone -> StartDestination.Onboarding
            loggedIn        -> StartDestination.Home
            else            -> StartDestination.Landing
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StartDestination.Loading
    )

    fun completeOnboarding() {
        viewModelScope.launch { pref.setOnboardingDone() }
    }
}
