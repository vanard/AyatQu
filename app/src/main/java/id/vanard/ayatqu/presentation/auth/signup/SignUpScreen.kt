package id.vanard.ayatqu.presentation.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.presentation.common.component.AppIcon
import id.vanard.ayatqu.presentation.auth.component.AuthBackground
import id.vanard.ayatqu.presentation.auth.component.AuthInputField
import id.vanard.ayatqu.presentation.auth.component.BackButtonDark
import id.vanard.ayatqu.presentation.auth.component.OrDivider
import id.vanard.ayatqu.presentation.auth.component.SocialButton
import id.vanard.ayatqu.presentation.auth.signup.contract.OnSignUpEvent
import id.vanard.ayatqu.presentation.auth.signup.contract.SignUpEvent
import id.vanard.ayatqu.presentation.auth.signup.contract.SignUpState

@Composable
fun SignUpScreen(
    state: SignUpState,
    onEvent: OnSignUpEvent,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Box(modifier = modifier.fillMaxSize()) {
        AuthBackground()

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top area ──────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AppIcon()
            }

            // ── Bottom sheet ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(AyatQuTheme.colors.surface)
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp)
                    .padding(top = 32.dp, bottom = 40.dp),
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BackButtonDark(onClick = { onEvent(SignUpEvent.BackClicked) })
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.signup),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = AyatQuTheme.colors.textPrimary
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.create_your_account),
                    style = MaterialTheme.typography.headlineSmall,
                    color = AyatQuTheme.colors.textPrimary
                )

                Spacer(Modifier.height(24.dp))

                // Email
                AuthInputField(
                    label = stringResource(R.string.email_address),
                    value = state.email,
                    onValueChange = { onEvent(SignUpEvent.EmailChanged(it)) },
                    placeholder = stringResource(R.string.email_placeholder),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(16.dp))

                // Password
                AuthInputField(
                    label = stringResource(R.string.password),
                    value = state.password,
                    onValueChange = { onEvent(SignUpEvent.PasswordChanged(it)) },
                    placeholder = stringResource(R.string.password_placeholder),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (state.passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { onEvent(SignUpEvent.PasswordVisibilityClicked) }) {
                            Text(
                                text = if (state.passwordVisible) stringResource(R.string.hide) else stringResource(R.string.show),
                                style = MaterialTheme.typography.labelSmall,
                                color = AyatQuTheme.colors.primary
                            )
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))

                // Error text
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Continue CTA
                Button(
                    onClick = { onEvent(SignUpEvent.SubmitClicked) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AyatQuTheme.colors.primary,
                        contentColor = AyatQuTheme.colors.onPrimary,
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = AyatQuTheme.colors.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.continue_button),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                OrDivider()

                Spacer(Modifier.height(16.dp))

                SocialButton(
                    label = stringResource(R.string.continue_with_google),
                    onClick = { onEvent(SignUpEvent.GoogleClicked) }
                )

                Spacer(Modifier.height(12.dp))

                SocialButton(
                    label = stringResource(R.string.continue_with_apple),
                    onClick = { /* TODO: Apple sign-in */ }
                )

                Spacer(Modifier.height(24.dp))

                // Log in link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.has_account_prompt),
                        style = MaterialTheme.typography.bodySmall,
                        color = AyatQuTheme.colors.textMuted
                    )
                    TextButton(onClick = { onEvent(SignUpEvent.LoginClicked) }) {
                        Text(
                            text = stringResource(R.string.log_in),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = AyatQuTheme.colors.primary
                        )
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true, name = "SignUp - Dark")
@Composable
private fun PreviewSignUpDark() {
    AyatQuTheme(darkTheme = true) {
        SignUpScreen(state = SignUpState(), onEvent = {})
    }
}

@Preview(showBackground = true, name = "SignUp - Light")
@Composable
private fun PreviewSignUpLight() {
    AyatQuTheme(darkTheme = false) {
        SignUpScreen(state = SignUpState(), onEvent = {})
    }
}
