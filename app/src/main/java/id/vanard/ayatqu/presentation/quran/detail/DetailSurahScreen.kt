package id.vanard.ayatqu.presentation.quran.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.domain.model.Ayah
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.core.ui.icon.ArrowLeft
import id.vanard.ayatqu.core.ui.icon.Download
import id.vanard.ayatqu.core.ui.icon.Pause
import id.vanard.ayatqu.core.ui.icon.Play
import id.vanard.ayatqu.presentation.quran.detail.contract.DetailSurahEvent
import id.vanard.ayatqu.presentation.quran.detail.contract.DetailSurahState
import id.vanard.ayatqu.presentation.quran.detail.contract.OnDetailSurahEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSurahScreen(
    state: DetailSurahState,
    onEvent: OnDetailSurahEvent,
    modifier: Modifier = Modifier,
    listState: androidx.compose.foundation.lazy.LazyListState = rememberLazyListState(),
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpAyahInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState,
        canScroll = { state.ayahs.isNotEmpty() },
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, top = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = state.surah?.nameEnglish ?: "",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = AyatQuTheme.colors.textPrimary,
                            lineHeight = 18.sp,
                            maxLines = 1,
                        )
                        Spacer(Modifier.height(0.5.dp))
                        Text(
                            text = state.surah?.nameArabic ?: "",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AyatQuTheme.colors.accentGold,
                            maxLines = 1,
                        )
                        val collapsedFraction by animateFloatAsState(
                            targetValue = topAppBarState.collapsedFraction,
                            animationSpec = tween(200),
                            label = "collapsedFraction",
                        )
                        if (collapsedFraction <= 0.2f) {
                            Text(
                                text = buildString {
                                    state.surah?.let {
                                        append(
                                            stringResource(
                                                R.string.ayahs_count,
                                                it.versesCount
                                            )
                                        )
                                    }
                                    state.surah?.let { append(" . ${it.revelationPlace.replaceFirstChar { c -> c.uppercase() }}") }
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AyatQuTheme.colors.primary,
                                letterSpacing = 0.6.sp,
                                lineHeight = 14.sp,
                                maxLines = 1,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DetailSurahEvent.BackClicked) }) {
                        Icon(
                            imageVector = ArrowLeft,
                            contentDescription = stringResource(R.string.back),
                            tint = AyatQuTheme.colors.textPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ellipsis_vertical_stroke_rounded),
                                contentDescription = stringResource(R.string.more),
                                tint = AyatQuTheme.colors.textPrimary,
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
                                        text = stringResource(R.string.jump_to_ayah),
                                        fontWeight = FontWeight.Medium,
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    showJumpDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_circle_up_stroke_rounded),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                    )
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (state.isDownloadingAll) stringResource(R.string.downloading) else stringResource(
                                            R.string.download_all
                                        ),
                                        fontWeight = FontWeight.Medium,
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    if (!state.isDownloadingAll) {
                                        onEvent(DetailSurahEvent.DownloadAllClicked)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Download,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                    )
                                },
                                enabled = !state.isDownloadingAll,
                            )
                        }
                    }
                },
//                windowInsets = if (topAppBarState.collapsedFraction > 0.5f)
//                    TopAppBarDefaults.windowInsets
//                else
//                    WindowInsets(0, 64, 0, 0),
                windowInsets = TopAppBarDefaults.windowInsets,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AyatQuTheme.colors.surface,
                    scrolledContainerColor = AyatQuTheme.colors.surface,
                ),
            )
        },
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = AyatQuTheme.colors.primary,
                    modifier = Modifier.size(40.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = padding,
            ) {
                // Download progress
                if (state.isDownloadingAll && state.downloadProgress != null) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AyatQuTheme.colors.surface)
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                        ) {
                            LinearProgressIndicator(
                                progress = { state.downloadProgress.first.toFloat() / state.downloadProgress.second },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = AyatQuTheme.colors.accentGold,
                                trackColor = AyatQuTheme.colors.border,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(
                                    R.string.downloading_progress,
                                    state.downloadProgress.first,
                                    state.downloadProgress.second
                                ),
                                fontSize = 11.sp,
                                color = AyatQuTheme.colors.textMuted,
                            )
                        }
                    }
                }

                // Bismillah
                if (state.surah?.bismillahPre == true) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AyatQuTheme.colors.surface)
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "\u0628\u0650\u0633\u0652\u0645\u0650 \u0627\u0644\u0652\u0631\u0651\u064E\u0645\u0652\u0639\u0650 \u0627\u0644\u0652\u0631\u0651\u064E\u062D\u0652\u0645\u064E\u0646\u0650\u064A",
                                fontSize = 22.sp,
                                color = AyatQuTheme.colors.accentGold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        HorizontalDivider(color = AyatQuTheme.colors.divider, thickness = 0.5.dp)
                    }
                }

                // Ayahs
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
                        isLastRead = state.currentLastRead?.let { lastRead ->
                            lastRead.surahNumber == ayah.surahNumber &&
                                lastRead.ayahNumber == ayah.ayahNumber
                        } == true,
                        onPlayClick = { onEvent(DetailSurahEvent.PlayAyahClicked(ayah.ayahNumber)) },
                        onDownloadClick = { onEvent(DetailSurahEvent.DownloadAyahClicked(ayah.ayahNumber)) },
                        onSetLastRead = { onEvent(DetailSurahEvent.SetLastReadClicked(ayah.ayahNumber)) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = AyatQuTheme.colors.border,
                        thickness = 0.5.dp,
                    )
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }

    // Overwrite confirmation dialog
    if (state.showOverwriteDialog && state.pendingAyahNumber != null) {
        AlertDialog(
            onDismissRequest = { onEvent(DetailSurahEvent.DismissOverwriteClicked) },
            title = { Text(text = stringResource(R.string.overwrite_last_read)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.overwrite_last_read_message,
                        state.currentLastRead?.surahName ?: "",
                        state.surah?.nameEnglish ?: "",
                        state.pendingAyahNumber,
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { onEvent(DetailSurahEvent.ConfirmOverwriteClicked) }) {
                    Text(text = stringResource(R.string.overwrite))
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(DetailSurahEvent.DismissOverwriteClicked) }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
        )
    }

    // Jump to ayah dialog
    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = {
                showJumpDialog = false
                jumpAyahInput = ""
            },
            title = { Text(text = stringResource(R.string.jump_to_ayah)) },
            text = {
                OutlinedTextField(
                    value = jumpAyahInput,
                    onValueChange = { jumpAyahInput = it.filter { c -> c.isDigit() } },
                    label = { Text(text = stringResource(R.string.ayah_number_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val ayahNum = jumpAyahInput.toIntOrNull()
                        if (ayahNum != null && ayahNum in 1..state.ayahs.size) {
                            val index = state.ayahs.indexOfFirst { it.ayahNumber == ayahNum }
                            if (index >= 0) {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index)
                                }
                            }
                        }
                        showJumpDialog = false
                        jumpAyahInput = ""
                    },
                    enabled = jumpAyahInput.toIntOrNull() != null,
                ) {
                    Text(text = stringResource(R.string.jump))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showJumpDialog = false
                        jumpAyahInput = ""
                    },
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun AyahCard(
    ayah: Ayah,
    isPlaying: Boolean,
    isPreparingAudio: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    isLastRead: Boolean,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onSetLastRead: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPlaying) AyatQuTheme.colors.accentGold.copy(alpha = 0.08f)
                else AyatQuTheme.colors.surface
            )
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
                    .background(AyatQuTheme.colors.surfaceVariant)
                    .border(1.dp, AyatQuTheme.colors.border, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ayah.ayahNumber.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AyatQuTheme.colors.primary,
                )
            }

            Spacer(Modifier.width(12.dp))

            // Arabic text
            Text(
                text = ayah.arabic,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                color = AyatQuTheme.colors.textPrimary,
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
                color = AyatQuTheme.colors.textMuted,
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
                color = AyatQuTheme.colors.textSecondary,
                lineHeight = 22.sp,
            )
        }

        Spacer(Modifier.height(10.dp))

        // Action buttons: last read + download/play
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Last read button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLastRead) AyatQuTheme.colors.primary else AyatQuTheme.colors.selectedSurface
                    )
                    .clickable { onSetLastRead() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isLastRead) {
                            R.drawable.bookmark_solid_rounded
                        } else {
                            R.drawable.bookmark_stroke_rounded
                        }
                    ),
                    contentDescription = stringResource(R.string.set_last_read),
                    tint = if (isLastRead) AyatQuTheme.colors.onPrimary else AyatQuTheme.colors.primary,
                    modifier = Modifier.size(16.dp),
                )
            }

            Spacer(Modifier.width(8.dp))

            // Play/Download button
            val buttonColor = when {
                isPlaying -> AyatQuTheme.colors.accentGold
                isDownloaded -> AyatQuTheme.colors.primary
                else -> AyatQuTheme.colors.selectedSurface
            }
            val iconTint = when {
                isPlaying -> AyatQuTheme.colors.onPrimary
                isDownloaded -> AyatQuTheme.colors.onPrimary
                else -> AyatQuTheme.colors.primary
            }
            val clickable = isDownloaded || (!isDownloading)

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(buttonColor)
                    .then(
                        if (clickable) Modifier.clickable {
                            if (isDownloaded) onPlayClick() else onDownloadClick()
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    isDownloading || isPreparingAudio -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = if (isPreparingAudio) AyatQuTheme.colors.onPrimary else AyatQuTheme.colors.primary,
                            strokeWidth = 2.dp,
                        )
                    }

                    isPlaying -> {
                        Icon(
                            imageVector = Pause,
                            contentDescription = stringResource(R.string.pause),
                            tint = AyatQuTheme.colors.onPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    isDownloaded -> {
                        Icon(
                            imageVector = Play,
                            contentDescription = stringResource(R.string.play),
                            tint = AyatQuTheme.colors.onPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    else -> {
                        Icon(
                            imageVector = Download,
                            contentDescription = stringResource(R.string.download),
                            tint = AyatQuTheme.colors.primary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
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
        DetailSurahScreen(
            state = DetailSurahState(
                isLoading = false,
                surah = previewSurah,
                ayahs = previewAyahs,
                playingAyah = 2,
                downloadedAyahs = setOf(1),
                isPreparingAudio = false,
                currentLastRead = LastRead(
                    surahNumber = 1,
                    ayahNumber = 1,
                    surahName = "Al-Fatihah",
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Detail Surah - Loading")
@Composable
private fun PreviewDetailSurahLoading() {
    AyatQuTheme(darkTheme = false) {
        DetailSurahScreen(
            state = DetailSurahState(isLoading = true),
            onEvent = {},
        )
    }
}
