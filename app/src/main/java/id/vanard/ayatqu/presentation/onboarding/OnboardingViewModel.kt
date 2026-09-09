package id.vanard.ayatqu.presentation.onboarding

import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.data.OnboardingPreference
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel
import id.vanard.ayatqu.presentation.onboarding.contract.OnboardingEvent
import id.vanard.ayatqu.presentation.onboarding.contract.OnboardingSideEffect
import id.vanard.ayatqu.presentation.onboarding.contract.OnboardingState
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val onboardingPreference: OnboardingPreference,
) : BaseMviViewModel<OnboardingState, OnboardingEvent, OnboardingSideEffect>(OnboardingState()) {
    override fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.PageChanged -> setState { copy(page = event.page.coerceIn(0, LAST_PAGE)) }
            OnboardingEvent.BackClicked -> setState { copy(page = (page - 1).coerceAtLeast(0)) }
            OnboardingEvent.NextClicked -> {
                if (uiState.value.page == LAST_PAGE) finishOnboarding()
                else setState { copy(page = page + 1) }
            }
            OnboardingEvent.SkipClicked -> finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        viewModelScope.launch {
            onboardingPreference.setOnboardingDone()
            setEffect(OnboardingSideEffect.NavigateToLanding)
        }
    }

    private companion object {
        const val LAST_PAGE = 2
    }
}
