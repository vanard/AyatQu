package id.vanard.ayatqu.presentation.common.component

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.core.ui.icon.WifiSlash

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
            .background(AyatQuTheme.colors.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = WifiSlash,
            contentDescription = null,
            tint = AyatQuTheme.colors.textMuted,
            modifier = Modifier.size(64.dp),
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.no_internet_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AyatQuTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.no_internet_message),
            fontSize = 14.sp,
            color = AyatQuTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AyatQuTheme.colors.primary)
                .clickable(onClick = onRetry)
                .padding(horizontal = 32.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.try_again),
                color = AyatQuTheme.colors.onPrimary,
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
