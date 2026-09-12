package id.vanard.ayatqu.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.presentation.onboarding.contract.OnOnboardingEvent
import id.vanard.ayatqu.presentation.onboarding.contract.OnboardingEvent
import id.vanard.ayatqu.presentation.onboarding.contract.OnboardingState

private data class OnboardingPage(
    val titleRes: Int,
    val subtitleRes: Int,
    val ctaLabelRes: Int,
)

private val pages = listOf(
    OnboardingPage(
        titleRes = R.string.onboarding_title_1,
        subtitleRes = R.string.onboarding_subtitle_1,
        ctaLabelRes = R.string.onboarding_get_started,
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_title_2,
        subtitleRes = R.string.onboarding_subtitle_2,
        ctaLabelRes = R.string.next,
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_title_3,
        subtitleRes = R.string.onboarding_subtitle_3,
        ctaLabelRes = R.string.next,
    ),
)

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onEvent: OnOnboardingEvent,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { pages.size }

    LaunchedEffect(state.page) {
        if (pagerState.currentPage != state.page) {
            pagerState.animateScrollToPage(state.page)
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        if (state.page != pagerState.currentPage) {
            onEvent(OnboardingEvent.PageChanged(pagerState.currentPage))
        }
    }

    Box(
        modifier = modifier
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
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            PageContent(page = pages[index])
        }

        // Static header — does not scroll with pages
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp, start = 32.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = AyatQuTheme.colors.onPrimary
            )
            TextButton(onClick = { onEvent(OnboardingEvent.SkipClicked) }) {
                Text(
                    text = stringResource(R.string.skip),
                    style = MaterialTheme.typography.labelMedium,
                    color = AyatQuTheme.colors.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        // Bottom controls
        val currentPage = pages[pagerState.currentPage]
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PaginationDots(
                count = pages.size,
                current = pagerState.currentPage,
                activeColor = AyatQuTheme.colors.primary
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val backButtonWidth by animateDpAsState(
                    targetValue = if (pagerState.currentPage > 0) 56.dp else 0.dp,
                    animationSpec = tween(300),
                    label = "back_width"
                )
                val backButtonAlpha by animateFloatAsState(
                    targetValue = if (pagerState.currentPage > 0) 1f else 0f,
                    animationSpec = tween(300),
                    label = "back_alpha"
                )
                if (backButtonWidth > 0.dp) {
                    Box(
                        modifier = Modifier
                            .width(backButtonWidth)
                            .graphicsLayer { alpha = backButtonAlpha }
                    ) {
                        BackCircleButton(
                            onClick = { onEvent(OnboardingEvent.BackClicked) }
                        )
                    }
                    Spacer(
                        Modifier
                            .width(16.dp * backButtonAlpha)
                    )
                }

                Button(
                    onClick = { onEvent(OnboardingEvent.NextClicked) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = if (pagerState.currentPage == 0) RoundedCornerShape(16.dp) else CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = AyatQuTheme.colors.primary)
                ) {
                    Text(
                        text = stringResource(currentPage.ctaLabelRes),
                        style = MaterialTheme.typography.labelLarge,
                        color = AyatQuTheme.colors.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun PageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(120.dp)) // space for static header

        // Mosque illustration area — replace Box content with actual image asset
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(AyatQuTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🕌",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }

        // Section heading and subtitle are rendered in the overlay via bottom controls padding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(page.titleRes),
                style = MaterialTheme.typography.headlineMedium,
                color = AyatQuTheme.colors.onPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(page.subtitleRes),
                style = MaterialTheme.typography.bodyLarge,
                color = AyatQuTheme.colors.onPrimary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BackCircleButton(onClick: () -> Unit) {
    val arrowColor = AyatQuTheme.colors.onPrimary
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(AyatQuTheme.colors.onPrimary.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .drawWithCache {
                val strokePx = 2.dp.toPx()
                val arrowSize = 16.dp.toPx()
                val cx = size.width / 2f
                val cy = size.height / 2f
                val path = Path().apply {
                    // shaft
                    moveTo(cx + arrowSize / 2f, cy)
                    lineTo(cx - arrowSize / 2f, cy)
                    // head top
                    moveTo(cx - arrowSize / 2f, cy)
                    lineTo(cx - arrowSize / 2f + arrowSize * 0.4f, cy - arrowSize * 0.4f)
                    // head bottom
                    moveTo(cx - arrowSize / 2f, cy)
                    lineTo(cx - arrowSize / 2f + arrowSize * 0.4f, cy + arrowSize * 0.4f)
                }
                onDrawWithContent {
                    drawContent()
                    drawPath(path, arrowColor, style = Stroke(width = strokePx))
                }
            }
    )
}

@Composable
private fun PaginationDots(count: Int, current: Int, activeColor: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { index ->
            val isActive = index == current
            val width by animateDpAsState(targetValue = if (isActive) 24.dp else 8.dp, label = "dot_$index")
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(if (isActive) activeColor else AyatQuTheme.colors.disabled)
            )
        }
    }
}
@Preview(showBackground = true, name = "Onboarding - Dark")
@Composable
private fun PreviewOnboardingDark() {
    AyatQuTheme(darkTheme = true) {
        OnboardingScreen(state = OnboardingState(), onEvent = {})
    }
}

@Preview(showBackground = true, name = "Onboarding - Light")
@Composable
private fun PreviewOnboardingLight() {
    AyatQuTheme(darkTheme = false) {
        OnboardingScreen(state = OnboardingState(), onEvent = {})
    }
}
