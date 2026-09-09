package id.vanard.ayatqu.presentation.auth.signup.contract

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
