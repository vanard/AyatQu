package id.vanard.ayatqu.presentation.auth.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.presentation.common.component.AppIcon
import id.vanard.ayatqu.presentation.auth.landing.contract.LandingEvent
import id.vanard.ayatqu.presentation.auth.landing.contract.LandingState
import id.vanard.ayatqu.presentation.auth.landing.contract.OnLandingEvent

@Composable
fun LandingScreen(
    state: LandingState,
    onEvent: OnLandingEvent,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {

        // ── Background gradient ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AyatQuTheme.colors.authBackgroundStart,
                            AyatQuTheme.colors.authBackgroundEnd,
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
        )

        // ── Foreground content ────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {

            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AyatQuTheme.colors.onPrimary.copy(alpha = 0.8f),
                    modifier = Modifier
                        .clickable { onEvent(LandingEvent.SkipClicked) }
                        .padding(8.dp)
                )
            }

            // ── Center: App icon + label ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // App icon
                AppIcon()

                Spacer(Modifier.height(24.dp))

                // App name
                Text(
                    text = stringResource(R.string.landing_headline),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AyatQuTheme.colors.onPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.2).sp,
                )
            }

            // ── Bottom sheet ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(AyatQuTheme.colors.surface)
                    .navigationBarsPadding()
                    .padding(horizontal = 32.dp)
                    .padding(top = 32.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))

                // Log In
                Button(
                    onClick = { onEvent(LandingEvent.LoginClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AyatQuTheme.colors.primary,
                        contentColor = AyatQuTheme.colors.onPrimary,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.log_in),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Sign Up
                OutlinedButton(
                    onClick = { onEvent(LandingEvent.SignUpClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AyatQuTheme.colors.surface,
                        contentColor = AyatQuTheme.colors.textPrimary,
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyatQuTheme.colors.border)
                ) {
                    Text(
                        text = stringResource(R.string.sign_up),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

                Spacer(Modifier.height(40.dp))

                // Legal
                Text(
                    text = stringResource(R.string.landing_legal),
                    style = MaterialTheme.typography.bodySmall,
                    color = AyatQuTheme.colors.textMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Landing - Dark")
@Composable
private fun PreviewLandingDark() {
    AyatQuTheme(darkTheme = true) {
        LandingScreen(state = LandingState, onEvent = {})
    }
}

@Preview(showBackground = true, name = "Landing - Light")
@Composable
private fun PreviewLandingLight() {
    AyatQuTheme(darkTheme = false) {
        LandingScreen(state = LandingState, onEvent = {})
    }
}
