package id.vanard.ayatqu.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.data.OnboardingPreference
import id.vanard.ayatqu.domain.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

sealed interface StartDestination {
    data object Loading : StartDestination
    data object Onboarding : StartDestination
    data object Landing : StartDestination
    data object Home : StartDestination
}

class AppViewModel(
    onboardingPreference: OnboardingPreference,
    authRepository: AuthRepository,
) : ViewModel() {
    private val isLoggedIn = flow { emit(authRepository.currentUser != null) }

    val startDestination: StateFlow<StartDestination> = combine(
        onboardingPreference.isOnboardingDone,
        isLoggedIn,
    ) { onboardingDone, loggedIn ->
        when {
            !onboardingDone -> StartDestination.Onboarding
            loggedIn -> StartDestination.Home
            else -> StartDestination.Landing
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StartDestination.Loading,
    )
}
