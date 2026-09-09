package id.vanard.ayatqu.presentation.auth.landing

import id.vanard.ayatqu.presentation.auth.landing.contract.LandingEvent
import id.vanard.ayatqu.presentation.auth.landing.contract.LandingSideEffect
import id.vanard.ayatqu.presentation.auth.landing.contract.LandingState
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel

class LandingViewModel : BaseMviViewModel<LandingState, LandingEvent, LandingSideEffect>(LandingState) {
    override fun onEvent(event: LandingEvent) {
        setEffect(
            when (event) {
                LandingEvent.LoginClicked -> LandingSideEffect.NavigateToLogin
                LandingEvent.SignUpClicked -> LandingSideEffect.NavigateToSignUp
                LandingEvent.SkipClicked -> LandingSideEffect.NavigateToHome
            }
        )
    }
}
