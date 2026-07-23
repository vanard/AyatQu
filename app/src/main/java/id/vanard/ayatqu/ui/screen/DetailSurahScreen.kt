package id.vanard.ayatqu.ui.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.domain.model.Ayah
import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.ui.icons.ArrowLeft
import id.vanard.ayatqu.ui.icons.Check
import id.vanard.ayatqu.ui.icons.DotsThreeVertical
import id.vanard.ayatqu.ui.icons.Download
import id.vanard.ayatqu.ui.icons.Pause
import id.vanard.ayatqu.ui.icons.Play
import id.vanard.ayatqu.viewmodel.DetailSurahUiState
import id.vanard.ayatqu.viewmodel.DetailSurahViewModel
import org.koin.androidx.compose.koinViewModel

// ── Design tokens ─────────────────────────────────────────────────────────────
private val ColorPrimary = Color(0xFF2D6B8C)
private val ColorTextPrimary = Color(0xFF2D2D2D)
private val ColorGold = Color(0xFFC19A6B)
private val ColorMuted = Color(0xFF8E8E93)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorBgSubtle = Color(0xFFF7F9FB)
private val ColorBorder = Color(0xFFF0F2F5)
private val ColorBadgeBg = Color(0xFFEFF6F9)

// ── Public composable ─────────────────────────────────────────────────────────

@Composable
fun DetailSurahScreen(
    surahNumber: Int,
    onBackClick: () -> Unit = {},
    viewModel: DetailSurahViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DetailSurahContent(
            state = state,
            onBackClick = onBackClick,
            onPlayAyah = viewModel::playAyah,
            onDownloadAyah = viewModel::downloadAyah,
            onDownloadAll = viewModel::downloadAll,
            onStopPlayback = viewModel::stopPlayback,
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

// ── Internal content ──────────────────────────────────────────────────────────

@Composable
internal fun DetailSurahContent(
    state: DetailSurahUiState,
    onBackClick: () -> Unit = {},
    onPlayAyah: (Int) -> Unit = {},
    onDownloadAyah: (Int) -> Unit = {},
    onDownloadAll: () -> Unit = {},
    onStopPlayback: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgSubtle)
            .statusBarsPadding(),
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        DetailSurahHeader(
            surah = state.surah,
            onBackClick = onBackClick,
            onDownloadAll = onDownloadAll,
            isDownloading = state.isDownloadingAll,
        )

        // ── Download progress ────────────────────────────────────────────────
        if (state.isDownloadingAll && state.downloadProgress != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorSurface)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                LinearProgressIndicator(
                    progress = { state.downloadProgress.first.toFloat() / state.downloadProgress.second },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = ColorGold,
                    trackColor = ColorBorder,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Downloading ${state.downloadProgress.first}/${state.downloadProgress.second}",
                    fontSize = 11.sp,
                    color = ColorMuted,
                )
            }
        }

        // ── Bismillah ────────────────────────────────────────────────────────
        if (state.surah?.bismillahPre == true) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorSurface)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u0628\u0650\u0633\u0652\u0645\u0650 \u0627\u0644\u0652\u0631\u0651\u064E\u0645\u0652\u0639\u0650 \u0627\u0644\u0652\u0631\u0651\u064E\u062D\u0652\u0645\u064E\u0646\u0650\u064A",
                    fontSize = 22.sp,
                    color = ColorGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
        }

        // ── Ayah list ────────────────────────────────────────────────────────
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = ColorPrimary,
                    modifier = Modifier.size(40.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                items(
                    items = state.ayahs,
                    key = { it.ayahNumber },
                ) { ayah ->
                    AyahCard(
                        ayah = ayah,
                        isPlaying = state.playingAyah == ayah.ayahNumber,
                        isPreparingAudio = state.isPreparingAudio && state.playingAyah == ayah.ayahNumber,
                        isDownloaded = ayah.ayahNumber in state.downloadedAyahs,
                        isDownloading = ayah.ayahNumber in state.downloadingAyahs,
                        onPlayClick = { onPlayAyah(ayah.ayahNumber) },
                        onDownloadClick = { onDownloadAyah(ayah.ayahNumber) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = ColorBorder,
                        thickness = 0.5.dp,
                    )
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun DetailSurahHeader(
    surah: Surah?,
    onBackClick: () -> Unit,
    onDownloadAll: () -> Unit,
    isDownloading: Boolean,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurface)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = ArrowLeft,
                contentDescription = "Back",
                tint = ColorTextPrimary,
                modifier = Modifier.size(24.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = surah?.nameEnglish ?: "",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = surah?.nameArabic ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorGold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = buildString {
                    surah?.let { append("${it.versesCount} Ayahs") }
                    surah?.let { append(" . ${it.revelationPlace.replaceFirstChar { c -> c.uppercase() }}") }
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorPrimary,
                letterSpacing = 0.6.sp,
            )
        }

        // Overflow menu
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = DotsThreeVertical,
                    contentDescription = "More",
                    tint = ColorTextPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (isDownloading) "Downloading..." else "Download all",
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        if (!isDownloading) onDownloadAll()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    enabled = !isDownloading,
                )
            }
        }
    }
    HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
}

// ── Ayah card ─────────────────────────────────────────────────────────────────

@Composable
private fun AyahCard(
    ayah: Ayah,
    isPlaying: Boolean,
    isPreparingAudio: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isPlaying) ColorGold.copy(alpha = 0.06f) else ColorSurface)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Ayah number badge + Arabic text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            // Ayah number
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ColorBadgeBg)
                    .border(1.dp, ColorBorder, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ayah.ayahNumber.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorPrimary,
                )
            }

            Spacer(Modifier.width(12.dp))

            // Arabic text
            Text(
                text = ayah.arabic,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextPrimary,
                textAlign = TextAlign.End,
                lineHeight = 40.sp,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(8.dp))

        // Transliteration
        if (ayah.transliteration.isNotBlank()) {
            Text(
                text = ayah.transliteration,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = ColorMuted,
                lineHeight = 20.sp,
            )
            Spacer(Modifier.height(6.dp))
        }

        // Translation (English)
        val translation = ayah.translations["en"].orEmpty()
        if (translation.isNotBlank()) {
            Text(
                text = translation,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextPrimary.copy(alpha = 0.8f),
                lineHeight = 22.sp,
            )
        }

        Spacer(Modifier.height(10.dp))

        // Play / Download row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Download button
            if (isDownloaded) {
                Icon(
                    imageVector = Check,
                    contentDescription = "Downloaded",
                    tint = ColorGold,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
            } else if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = ColorGold,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ColorPrimary.copy(alpha = 0.08f))
                        .clickable(onClick = onDownloadClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Download,
                        contentDescription = "Download",
                        tint = ColorPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
            }

            // Play button
            val playAlpha = if (isDownloaded) 1f else 0.3f
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) ColorGold else ColorPrimary.copy(alpha = playAlpha))
                    .then(
                        if (isDownloaded) Modifier.clickable(onClick = onPlayClick)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isPreparingAudio) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = if (isPlaying) Pause else Play,
                        contentDescription = if (isPlaying) "Pause" else if (isDownloaded) "Play" else "Download ayah first",
                        tint = Color.White.copy(alpha = playAlpha),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

private val previewSurah = Surah(
    number = 1,
    nameArabic = "\u0627\u0644\u0641\u0627\u062A\u062D\u0629",
    nameEnglish = "Al-Fatihah",
    nameTranslation = "The Opening",
    revelationPlace = "makkah",
    versesCount = 7,
    bismillahPre = false,
)

private val previewAyahs = listOf(
    Ayah(
        surahNumber = 1, ayahNumber = 1, verseKey = "1:1",
        arabic = "\u0627\u0644\u062D\u0645\u062F \u0644\u0644\u0647 \u0627\u0644\u0639\u0627\u0644\u0645\u064A\u0646",
        transliteration = "Alhamdu lillahi rabbil alamin",
        translations = mapOf("en" to "All praise is due to Allah, Lord of the worlds."),
        audioUrls = emptyList(),
    ),
    Ayah(
        surahNumber = 1, ayahNumber = 2, verseKey = "1:2",
        arabic = "\u0627\u0644\u0631\u062D\u0645\u064E\u0646\u0650 \u0627\u0644\u0631\u0651\u064E\u062D\u0652\u0645\u064E\u064A\u0646",
        transliteration = "Ar-rahmanir-rahim",
        translations = mapOf("en" to "The Entirely Merciful, the Especially Merciful."),
        audioUrls = emptyList(),
    ),
    Ayah(
        surahNumber = 1, ayahNumber = 3, verseKey = "1:3",
        arabic = "\u0645\u0627\u0644\u064E\u0643\u0650 \u064A\u0648\u0645\u0650 \u0627\u0644\u062F\u0650\u064A\u0646",
        transliteration = "Maliki yawmid-din",
        translations = mapOf("en" to "Sovereign of the Day of Recompense."),
        audioUrls = emptyList(),
    ),
)

@Preview(showBackground = true, name = "Detail Surah")
@Composable
private fun PreviewDetailSurah() {
    AyatQuTheme(darkTheme = false) {
        DetailSurahContent(
            state = DetailSurahUiState(
                isLoading = false,
                surah = previewSurah,
                ayahs = previewAyahs,
                playingAyah = 2,
                downloadedAyahs = setOf(1),
                isPreparingAudio = false,
            ),
        )
    }
}

@Preview(showBackground = true, name = "Detail Surah - Loading")
@Composable
private fun PreviewDetailSurahLoading() {
    AyatQuTheme(darkTheme = false) {
        DetailSurahContent(
            state = DetailSurahUiState(isLoading = true),
        )
    }
}
