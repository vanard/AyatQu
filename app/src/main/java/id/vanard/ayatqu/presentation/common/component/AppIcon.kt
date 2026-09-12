package id.vanard.ayatqu.presentation.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme

@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    iconSize: Dp = 100.dp,
    backgroundColor: Color? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(24.dp, CircleShape)
            .clip(CircleShape)
            .background(backgroundColor ?: AyatQuTheme.colors.logoBackground),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Preview
@Composable
private fun AppIconPreview() {
    AyatQuTheme {
        AppIcon()
    }
}
