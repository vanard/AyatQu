package id.vanard.ayatqu.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.PrayerTime
import id.vanard.ayatqu.ui.icons.Info
import id.vanard.ayatqu.ui.icons.Moon
import id.vanard.ayatqu.util.LocationHelper
import id.vanard.ayatqu.viewmodel.HomeUiState
import id.vanard.ayatqu.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

// ── Design tokens ─────────────────────────────────────────────────────────────
private val ColorPrimary      = Color(0xFF2D6B8C)
private val ColorTextPrimary  = Color(0xFF2D2D2D)
private val ColorGold         = Color(0xFFC19A6B)
private val ColorMuted        = Color(0xFF8E8E93)
private val ColorBorder       = Color(0xFFF9FAFB)
private val ColorCardGradient = listOf(Color(0xFFF5E6CC), Color(0xFFF8F1E4), Color(0xFFFFFFFF))
private val ColorCardBorder   = Color(0xFFEBE3D5)
private val ColorBgSubtle     = Color(0xFFF7F9FB)
private val ColorSurface      = Color(0xFFFFFFFF)
private val ColorDivider      = Color(0xFFF0F2F5)

// ── Public composable ─────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    authViewModel: id.vanard.ayatqu.viewmodel.AuthViewModel = koinViewModel(),
    onLastReadClick: (surahNumber: Int, ayahNumber: Int) -> Unit = { _, _ -> },
) {
    val state by viewModel.uiState.collectAsState()
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Get user display name
    val userName = authState.user?.displayName.orEmpty().ifEmpty {
        authState.user?.email?.substringBefore("@").orEmpty().ifEmpty { "Guest" }
    }

    // Location permission launcher — requests once when permission is not granted
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            viewModel.loadPrayerTimesWithLocation(fetchLocation = true)
        } else {
            // Permission denied — skip location, use cache or city fallback
            viewModel.loadPrayerTimesWithLocation(fetchLocation = false)
        }
    }

    // Request location permission on first composition
    LaunchedEffect(Unit) {
        if (LocationHelper.isLocationPermissionGranted(context)) {
            viewModel.loadPrayerTimesWithLocation(fetchLocation = true)
        } else {
            locationPermissionLauncher.launch(LocationHelper.getLocationPermissions())
        }
    }

    HomeScreenContent(
        state = state,
        isNetworkAvailable = isNetworkAvailable,
        userName = userName,
        onLastReadClick = onLastReadClick,
        onRetry = viewModel::retryPrayerTimes,
    )
}

// ── Internal content (previewable) ────────────────────────────────────────────

@Composable
internal fun HomeScreenContent(
    state: HomeUiState,
    isNetworkAvailable: Boolean = true,
    userName: String = "Guest",
    onLastReadClick: (surahNumber: Int, ayahNumber: Int) -> Unit = { _, _ -> },
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgSubtle)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        HomeHeader(userName = userName)

        Spacer(Modifier.height(8.dp))

        // ── Last Read Card ────────────────────────────────────────────────────
        if (state.lastRead != null) {
            LastReadCard(
                lastRead = state.lastRead,
                onClick = {
                    onLastReadClick(state.lastRead.surahNumber, state.lastRead.ayahNumber)
                },
            )
            Spacer(Modifier.height(24.dp))
        }

        // ── Prayer Times Section ──────────────────────────────────────────────
        PrayerTimesSection(
            prayerTimes = state.prayerTimes,
            isLoading = state.isPrayerTimesLoading,
            error = state.prayerTimesError,
            isNetworkAvailable = isNetworkAvailable,
            locationLabel = state.locationLabel,
            locationError = state.locationError,
            isLocationLoading = state.isLocationLoading,
            onRetry = onRetry,
        )

        Spacer(Modifier.height(24.dp))
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(userName: String) {
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
                text = "Welcome, ${userName.ifEmpty { "Guest" }}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Assalamualaikum",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ColorMuted,
            )
        }

        IconPlaceholder(size = 32)
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
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(ColorCardGradient))
            .border(1.dp, ColorCardBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 25.dp, vertical = 20.dp),
    ) {
        // Decorative mosque silhouette placeholder — right side
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 160.dp, height = 100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ColorGold.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("🕌", fontSize = 48.sp)
        }

        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = 120.dp),
        ) {
            Text(
                text = "Last Read",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ColorGold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = lastRead.surahName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "AYAH NO: ${lastRead.ayahNumber}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = ColorMuted,
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
    locationLabel: String,
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
            tint = ColorGold,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Prayer Times",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = locationLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = ColorMuted,
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
                message = "No internet connection",
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
                    color = ColorGold,
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
            .background(ColorSurface)
            .border(1.dp, ColorDivider, RoundedCornerShape(20.dp))
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
                            .background(ColorDivider),
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
            color = if (isNext) ColorPrimary else ColorTextPrimary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = time,
            fontSize = 15.sp,
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isNext) ColorPrimary else ColorTextPrimary,
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
            .background(ColorSurface)
            .border(1.dp, ColorDivider, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = ColorMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(ColorGold)
                .clickable(onClick = onRetry)
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Retry",
                color = Color.White,
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
            .background(ColorGold.copy(alpha = 0.1f))
            .border(1.dp, ColorGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Info,
            contentDescription = null,
            tint = ColorGold,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = message,
            fontSize = 13.sp,
            color = ColorTextPrimary,
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
            .background(ColorBorder),
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
        HomeScreenContent(
            state = HomeUiState(
                lastRead = LastRead(
                    surahNumber = 2,
                    ayahNumber = 255,
                    surahName = "Al-Baqarah",
                ),
                prayerTimes = samplePrayerTimes,
                locationLabel = "Jakarta",
            ),
            userName = "Abdullah",
        )
    }
}

@Preview(showBackground = true, name = "Home - Guest User")
@Composable
private fun PreviewHomeGuest() {
    AyatQuTheme(darkTheme = false) {
        HomeScreenContent(
            state = HomeUiState(
                lastRead = LastRead(
                    surahNumber = 1,
                    ayahNumber = 1,
                    surahName = "Al-Fatihah",
                ),
                prayerTimes = samplePrayerTimes,
                locationLabel = "Jakarta",
            ),
            userName = "Guest",
        )
    }
}

@Preview(showBackground = true, name = "Home - No Last Read")
@Composable
private fun PreviewHomeNoLastRead() {
    AyatQuTheme(darkTheme = false) {
        HomeScreenContent(
            state = HomeUiState(
                prayerTimes = samplePrayerTimes,
                locationLabel = "Jakarta",
            ),
            userName = "Abdullah",
        )
    }
}

@Preview(showBackground = true, name = "Home - Loading Prayer Times")
@Composable
private fun PreviewHomeLoading() {
    AyatQuTheme(darkTheme = false) {
        HomeScreenContent(
            state = HomeUiState(isPrayerTimesLoading = true),
            userName = "Guest",
        )
    }
}

@Preview(showBackground = true, name = "Home - Prayer Times Error")
@Composable
private fun PreviewHomeError() {
    AyatQuTheme(darkTheme = false) {
        HomeScreenContent(
            state = HomeUiState(prayerTimesError = "Failed to load prayer times"),
            userName = "Guest",
        )
    }
}

@Preview(showBackground = true, name = "Home - Location Error")
@Composable
private fun PreviewHomeLocationError() {
    AyatQuTheme(darkTheme = false) {
        HomeScreenContent(
            state = HomeUiState(
                locationError = "GPS is disabled. Please enable location services.",
                prayerTimes = samplePrayerTimes,
                locationLabel = "Jakarta",
            ),
            userName = "Guest",
        )
    }
}
