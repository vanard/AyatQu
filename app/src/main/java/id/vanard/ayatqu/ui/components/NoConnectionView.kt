package id.vanard.ayatqu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.ui.icons.WifiSlash

private val ColorPrimary = Color(0xFF2D6B8C)
private val ColorTextPrimary = Color(0xFF2D2D2D)
private val ColorMuted = Color(0xFF8E8E93)
private val ColorBgSubtle = Color(0xFFF7F9FB)

/**
 * Reusable "No Connection" screen shown when the device is offline.
 *
 * Drop this into any screen that needs API access:
 * ```
 * if (!isNetworkAvailable) {
 *     NoConnectionView(onRetry = viewModel::loadData)
 * }
 * ```
 */
@Composable
fun NoConnectionView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorBgSubtle)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = WifiSlash,
            contentDescription = null,
            tint = ColorMuted,
            modifier = Modifier.size(64.dp),
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "No Internet Connection",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Please check your network settings and try again.",
            fontSize = 14.sp,
            color = ColorMuted,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(ColorPrimary)
                .clickable(onClick = onRetry)
                .padding(horizontal = 32.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Try Again",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(showBackground = true, name = "No Connection")
@Composable
private fun NoConnectionViewPreview() {
    AyatQuTheme(darkTheme = false) {
        NoConnectionView(onRetry = {})
    }
}
