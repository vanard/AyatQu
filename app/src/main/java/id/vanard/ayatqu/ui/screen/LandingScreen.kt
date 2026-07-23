package id.vanard.ayatqu.ui.screen

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.core.ui.theme.BorderSubtle
import id.vanard.ayatqu.core.ui.theme.AyatQuSurface
import id.vanard.ayatqu.core.ui.theme.AyatQuTextDark
import id.vanard.ayatqu.core.ui.theme.TextHint

@Composable
fun LandingScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background image placeholder ──────────────────────────────────────
        // TODO: replace with actual mosque photo via painterResource / AsyncImage
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1C3A4A), Color(0xFF0A1A24)),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
        )

        // ── Dark scrim over image ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0x1A000000), Color(0x99000000)),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
        )

        // ── Foreground content ────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar with logo and skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.visible(false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                        // TODO: replace with actual logo icon
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Ayat Qu",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = Color.White,
                        letterSpacing = (-0.6).sp
                    )
                }

                // Skip button
                Text(
                    text = "Skip",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .clickable(onClick = onSkipClick)
                        .padding(8.dp)
                )
            }

            // Headline area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(Modifier.height(20.dp))

                // Headline
                Text(
                    text = "AYAT QU",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 39.6.sp,
                    letterSpacing = (-0.9).sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Bottom sheet ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 32.dp)
                    .padding(top = 32.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(12.dp))

                // Log In
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AyatQuSurface,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Log in",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Sign Up
                OutlinedButton(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = AyatQuTextDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Text(
                        text = "Sign up",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

                Spacer(Modifier.height(40.dp))

                // Legal
                Text(
                    text = "By continuing, you agree to our\nPrivacy Policy and Terms of Use",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextHint,
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
        LandingScreen(onLoginClick = {}, onSignUpClick = {}, onSkipClick = {})
    }
}

@Preview(showBackground = true, name = "Landing - Light")
@Composable
private fun PreviewLandingLight() {
    AyatQuTheme(darkTheme = false) {
        LandingScreen(onLoginClick = {}, onSignUpClick = {}, onSkipClick = {})
    }
}
