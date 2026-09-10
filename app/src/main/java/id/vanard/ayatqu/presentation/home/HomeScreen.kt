package id.vanard.ayatqu.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.PrayerTime
import id.vanard.ayatqu.core.ui.icon.Info
import id.vanard.ayatqu.core.ui.icon.Moon
import id.vanard.ayatqu.presentation.home.contract.HomeEvent
import id.vanard.ayatqu.presentation.home.contract.HomeState
import id.vanard.ayatqu.presentation.home.contract.OnHomeEvent

@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: OnHomeEvent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AyatQuTheme.colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        HomeHeader(
            userName = state.userName,
            onQiblaClick = { onEvent(HomeEvent.QiblaClicked) },
        )

        Spacer(Modifier.height(8.dp))

        // ── Last Read Card ────────────────────────────────────────────────────
        if (state.lastRead != null) {
            LastReadCard(
                lastRead = state.lastRead,
                onClick = {
                    onEvent(
                        HomeEvent.LastReadClicked(
                            surahNumber = state.lastRead.surahNumber,
                            ayahNumber = state.lastRead.ayahNumber,
                        )
                    )
                },
            )
            Spacer(Modifier.height(24.dp))
        }

        // ── Prayer Times Section ──────────────────────────────────────────────
        PrayerTimesSection(
            prayerTimes = state.prayerTimes,
            isLoading = state.isPrayerTimesLoading,
            error = state.prayerTimesError,
            isNetworkAvailable = state.isNetworkAvailable,
            timezone = state.timezone,
            locationError = state.locationError,
            isLocationLoading = state.isLocationLoading,
            onRetry = { onEvent(HomeEvent.RetryClicked) },
        )

        Spacer(Modifier.height(24.dp))
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(userName: String, onQiblaClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Greeting on the left
        Column {
            Text(
                text = stringResource(R.string.welcome_user, userName.ifEmpty { stringResource(R.string.guest) }),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AyatQuTheme.colors.primary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.assalamualaikum),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AyatQuTheme.colors.textMuted,
            )
        }

        // Compass icon — navigate to Qibla
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onQiblaClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_compass),
                contentDescription = "Qibla",
                tint = AyatQuTheme.colors.accentGold,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

// ── Last Read Card ────────────────────────────────────────────────────────────

@Composable
private fun LastReadCard(
    lastRead: LastRead,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        AyatQuTheme.colors.lastReadGradientStart,
                        AyatQuTheme.colors.lastReadGradientCenter,
                        AyatQuTheme.colors.lastReadGradientEnd,
                    )
                )
            )
            .border(1.dp, AyatQuTheme.colors.border, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
    ) {
        // Mosque silhouette — right side, semi-transparent
        Image(
            painter = painterResource(id = R.drawable.islamic_mosque_silhouette),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(150.dp)
                .offset(x = 10.dp),
            contentScale = ContentScale.Crop,
            alpha = 0.3f,
            colorFilter = ColorFilter.tint(AyatQuTheme.colors.accentGold),
        )

        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 25.dp, vertical = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.last_read),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AyatQuTheme.colors.textPrimary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = lastRead.surahName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AyatQuTheme.colors.textPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.ayah_no, lastRead.ayahNumber),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = AyatQuTheme.colors.textPrimary,
                letterSpacing = 0.6.sp,
            )
        }
    }
}

// ── Prayer Times Section ──────────────────────────────────────────────────────

@Composable
private fun PrayerTimesSection(
    prayerTimes: List<PrayerTime>,
    isLoading: Boolean,
    error: String?,
    isNetworkAvailable: Boolean,
    timezone: String? = null,
    locationError: String? = null,
    isLocationLoading: Boolean = false,
    onRetry: () -> Unit,
) {
    // Section header
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Moon,
            contentDescription = null,
            tint = AyatQuTheme.colors.accentGold,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.prayer_times),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AyatQuTheme.colors.textPrimary,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = timezone ?: stringResource(R.string.your_location),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AyatQuTheme.colors.textMuted,
        )
    }

    Spacer(Modifier.height(12.dp))

    // Location error banner (informational, not blocking)
    if (locationError != null && prayerTimes.isEmpty()) {
        LocationWarningCard(message = locationError)
        Spacer(Modifier.height(12.dp))
    }

    // Content
    when {
        !isNetworkAvailable && prayerTimes.isEmpty() -> {
            PrayerTimesErrorCard(
                message = stringResource(R.string.no_internet_connection),
                onRetry = onRetry,
            )
        }
        isLoading && prayerTimes.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = AyatQuTheme.colors.accentGold,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
        error != null && prayerTimes.isEmpty() -> {
            PrayerTimesErrorCard(
                message = error,
                onRetry = onRetry,
            )
        }
        prayerTimes.isNotEmpty() -> {
            PrayerTimesCard(prayerTimes = prayerTimes)
        }
    }
}

@Composable
private fun PrayerTimesCard(prayerTimes: List<PrayerTime>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AyatQuTheme.colors.surface)
            .border(1.dp, AyatQuTheme.colors.divider, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 4.dp),
    ) {
        prayerTimes.forEachIndexed { index, prayer ->
            PrayerTimeRow(
                name = prayer.name,
                time = prayer.time,
                isNext = index == findNextPrayerIndex(prayerTimes),
            )
            if (index < prayerTimes.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(AyatQuTheme.colors.divider),
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerTimeRow(
    name: String,
    time: String,
    isNext: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
            color = if (isNext) AyatQuTheme.colors.primary else AyatQuTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = time,
            fontSize = 15.sp,
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isNext) AyatQuTheme.colors.primary else AyatQuTheme.colors.textPrimary,
        )
    }
}

@Composable
private fun PrayerTimesErrorCard(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AyatQuTheme.colors.surface)
            .border(1.dp, AyatQuTheme.colors.divider, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = AyatQuTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AyatQuTheme.colors.accentGold)
                .clickable(onClick = onRetry)
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.retry),
                color = AyatQuTheme.colors.onPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LocationWarningCard(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AyatQuTheme.colors.accentGold.copy(alpha = 0.1f))
            .border(1.dp, AyatQuTheme.colors.accentGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Info,
            contentDescription = null,
            tint = AyatQuTheme.colors.accentGold,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = message,
            fontSize = 13.sp,
            color = AyatQuTheme.colors.textPrimary,
        )
    }
}

/**
 * Finds the index of the next upcoming prayer based on current time.
 * Returns 0 if all prayers have passed for the day.
 */
private fun findNextPrayerIndex(prayerTimes: List<PrayerTime>): Int {
    val now = java.util.Calendar.getInstance()
    val currentMinutes = now.get(java.util.Calendar.HOUR_OF_DAY) * 60 + now.get(java.util.Calendar.MINUTE)

    prayerTimes.forEachIndexed { index, prayer ->
        val parts = prayer.time.split(":")
        if (parts.size == 2) {
            val prayerMinutes = (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
            if (prayerMinutes > currentMinutes) return index
        }
    }
    return 0 // Default to first prayer if all passed
}

// ── Icon placeholder ───────────────────────────────────────────���──────────────

@Composable
private fun IconPlaceholder(size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AyatQuTheme.colors.border),
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val samplePrayerTimes = listOf(
    PrayerTime("Fajr", "04:32"),
    PrayerTime("Sunrise", "05:58"),
    PrayerTime("Dhuhr", "12:05"),
    PrayerTime("Asr", "15:28"),
    PrayerTime("Maghrib", "18:12"),
    PrayerTime("Isha", "19:24"),
)

@Preview(showBackground = true, name = "Home - With Last Read & Prayer Times")
@Composable
private fun PreviewHomeFull() {
    AyatQuTheme(darkTheme = false) {
        HomeScreen(
            state = HomeState(
                userName = "Abdullah",
                lastRead = LastRead(
                    surahNumber = 2,
                    ayahNumber = 255,
                    surahName = "Al-Baqarah",
                ),
                prayerTimes = samplePrayerTimes,
                timezone = "Asia/Jakarta",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Home - Guest User")
@Composable
private fun PreviewHomeGuest() {
    AyatQuTheme(darkTheme = false) {
        HomeScreen(
            state = HomeState(
                userName = "Guest",
                lastRead = LastRead(
                    surahNumber = 1,
                    ayahNumber = 1,
                    surahName = "Al-Fatihah",
                ),
                prayerTimes = samplePrayerTimes,
                timezone = "Asia/Jakarta",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Home - No Last Read")
@Composable
private fun PreviewHomeNoLastRead() {
    AyatQuTheme(darkTheme = false) {
        HomeScreen(
            state = HomeState(
                userName = "Abdullah",
                prayerTimes = samplePrayerTimes,
                timezone = "Asia/Jakarta",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Home - Loading Prayer Times")
@Composable
private fun PreviewHomeLoading() {
    AyatQuTheme(darkTheme = false) {
        HomeScreen(
            state = HomeState(isPrayerTimesLoading = true),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Home - Prayer Times Error")
@Composable
private fun PreviewHomeError() {
    AyatQuTheme(darkTheme = false) {
        HomeScreen(
            state = HomeState(prayerTimesError = "Failed to load prayer times"),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Home - Location Error")
@Composable
private fun PreviewHomeLocationError() {
    AyatQuTheme(darkTheme = false) {
        HomeScreen(
            state = HomeState(
                userName = "Guest",
                locationError = "GPS is disabled. Please enable location services.",
                prayerTimes = samplePrayerTimes,
                timezone = "Asia/Jakarta",
            ),
            onEvent = {},
        )
    }
}
