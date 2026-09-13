package id.vanard.ayatqu.presentation.quran.juz

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.icon.ArrowLeft
import id.vanard.ayatqu.core.ui.icon.CaretRight
import id.vanard.ayatqu.core.ui.icon.Download
import id.vanard.ayatqu.core.ui.icon.Pause
import id.vanard.ayatqu.core.ui.icon.Play
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.domain.model.Juz
import id.vanard.ayatqu.domain.model.JuzVerse
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.presentation.quran.translationFor
import id.vanard.ayatqu.presentation.quran.juz.contract.JuzDetailEvent
import id.vanard.ayatqu.presentation.quran.juz.contract.JuzDetailState
import id.vanard.ayatqu.presentation.quran.juz.contract.OnJuzDetailEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuzDetailScreen(
    state: JuzDetailState,
    onEvent: OnJuzDetailEvent,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpAyahInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AyatQuTheme.colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.juz_number, state.juzNumber),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = AyatQuTheme.colors.textPrimary,
                        )
                        state.juz?.let { juz ->
                            Text(
                                text = stringResource(R.string.juz_ayah_count, juz.totalVerses),
                                fontSize = 12.sp,
                                color = AyatQuTheme.colors.textMuted,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(JuzDetailEvent.BackClicked) }) {
                        Icon(
                            imageVector = ArrowLeft,
                            contentDescription = stringResource(R.string.back),
                            tint = AyatQuTheme.colors.textPrimary,
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                painter = painterResource(R.drawable.ellipsis_vertical_stroke_rounded),
                                contentDescription = stringResource(R.string.more),
                                tint = AyatQuTheme.colors.textPrimary,
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.jump_to_ayah)) },
                                onClick = {
                                    menuExpanded = false
                                    showJumpDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_circle_up_stroke_rounded),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                    )
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (state.isDownloadingAll) stringResource(R.string.downloading)
                                        else stringResource(R.string.download_all)
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEvent(JuzDetailEvent.DownloadAllClicked)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Download,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                    )
                                },
                                enabled = !state.isDownloadingAll && state.juz != null,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AyatQuTheme.colors.surface,
                ),
            )
        },
    ) { padding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = AyatQuTheme.colors.primary)
            }
            state.juz != null -> JuzVerses(
                state = state,
                onEvent = onEvent,
                listState = listState,
                modifier = Modifier.padding(padding),
            )
            else -> JuzError(
                message = state.errorMessage.orEmpty(),
                onRetry = { onEvent(JuzDetailEvent.RetryClicked) },
                modifier = Modifier.padding(padding),
            )
        }
    }

    val pendingVerse = state.pendingAyah
    if (state.showOverwriteDialog && pendingVerse != null) {
        AlertDialog(
            onDismissRequest = { onEvent(JuzDetailEvent.DismissOverwriteClicked) },
            title = { Text(stringResource(R.string.overwrite_last_read)) },
            text = {
                Text(
                    stringResource(
                        R.string.overwrite_last_read_message,
                        state.currentLastRead?.surahName.orEmpty(),
                        pendingVerse.surahName,
                        pendingVerse.ayahNumber,
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { onEvent(JuzDetailEvent.ConfirmOverwriteClicked) }) {
                    Text(stringResource(R.string.overwrite))
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(JuzDetailEvent.DismissOverwriteClicked) }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showJumpDialog) {
        val maxPosition = state.juz?.verses?.size ?: 0
        AlertDialog(
            onDismissRequest = {
                showJumpDialog = false
                jumpAyahInput = ""
            },
            title = { Text(stringResource(R.string.jump_to_ayah)) },
            text = {
                OutlinedTextField(
                    value = jumpAyahInput,
                    onValueChange = { jumpAyahInput = it.filter(Char::isDigit) },
                    label = { Text(stringResource(R.string.juz_ayah_position_hint)) },
                    supportingText = {
                        Text(stringResource(R.string.juz_ayah_position_range, maxPosition))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            },
            confirmButton = {
                val position = jumpAyahInput.toIntOrNull()
                TextButton(
                    onClick = {
                        if (position != null) {
                            coroutineScope.launch {
                                val progressOffset = if (state.isDownloadingAll) 1 else 0
                                listState.animateScrollToItem(position - 1 + progressOffset)
                            }
                        }
                        showJumpDialog = false
                        jumpAyahInput = ""
                    },
                    enabled = position != null && position in 1..maxPosition,
                ) {
                    Text(stringResource(R.string.jump))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showJumpDialog = false
                        jumpAyahInput = ""
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

private val JuzDetailState.pendingAyah: JuzVerse?
    get() = juz?.verses?.find { it.verseKey == pendingVerseKey }

@Composable
private fun JuzVerses(
    state: JuzDetailState,
    onEvent: OnJuzDetailEvent,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val juz = requireNotNull(state.juz)
    val translationLanguage = Locale.current.language
    LazyColumn(modifier = modifier.fillMaxSize(), state = listState) {
        state.downloadProgress?.let { (downloaded, total) ->
            item(key = "download_progress") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AyatQuTheme.colors.surface)
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                ) {
                    LinearProgressIndicator(
                        progress = { downloaded.toFloat() / total.coerceAtLeast(1) },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = AyatQuTheme.colors.accentGold,
                        trackColor = AyatQuTheme.colors.border,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.downloading_progress, downloaded, total),
                        fontSize = 11.sp,
                        color = AyatQuTheme.colors.textMuted,
                    )
                }
            }
        }
        itemsIndexed(
            items = juz.verses,
            key = { _, verse -> verse.verseKey },
        ) { index, verse ->
            if (index == 0 || juz.verses[index - 1].surahNumber != verse.surahNumber) {
                SurahSectionHeader(verse.surahName, verse.surahNumber)
            }
            JuzVerseCard(
                verse = verse,
                translationLanguage = translationLanguage,
                isPlaying = state.playingVerseKey == verse.verseKey,
                isPreparingAudio = state.isPreparingAudio && state.playingVerseKey == verse.verseKey,
                isDownloaded = verse.verseKey in state.downloadedVerseKeys,
                isDownloading = verse.verseKey in state.downloadingVerseKeys,
                isLastRead = state.currentLastRead?.let {
                    it.surahNumber == verse.surahNumber && it.ayahNumber == verse.ayahNumber
                } == true,
                onOpenSurah = {
                    onEvent(JuzDetailEvent.VerseClicked(verse.surahNumber, verse.ayahNumber))
                },
                onPlayClick = {
                    onEvent(JuzDetailEvent.PlayAyahClicked(verse.surahNumber, verse.ayahNumber))
                },
                onDownloadClick = {
                    onEvent(JuzDetailEvent.DownloadAyahClicked(verse.surahNumber, verse.ayahNumber))
                },
                onSetLastRead = {
                    onEvent(JuzDetailEvent.SetLastReadClicked(verse.surahNumber, verse.ayahNumber))
                },
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

@Composable
private fun SurahSectionHeader(name: String, number: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AyatQuTheme.colors.selectedSurface)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, fontWeight = FontWeight.Bold, color = AyatQuTheme.colors.primary)
        Text(
            text = number.toString().padStart(3, '0'),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = AyatQuTheme.colors.textMuted,
        )
    }
}

@Composable
private fun JuzVerseCard(
    verse: JuzVerse,
    translationLanguage: String,
    isPlaying: Boolean,
    isPreparingAudio: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    isLastRead: Boolean,
    onOpenSurah: () -> Unit,
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
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(AyatQuTheme.colors.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    verse.ayahNumber.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AyatQuTheme.colors.primary,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = verse.arabic,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontSize = 28.sp,
                lineHeight = 42.sp,
                color = AyatQuTheme.colors.textPrimary,
            )
        }

        if (verse.transliteration.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                verse.transliteration,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontStyle = FontStyle.Italic,
                color = AyatQuTheme.colors.textMuted,
            )
        }

        val translation = verse.translations.translationFor(translationLanguage)
        if (translation.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                translation,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = AyatQuTheme.colors.textSecondary,
            )
        }

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.clip(CircleShape).clickable(onClick = onOpenSurah)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.open_in_surah),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AyatQuTheme.colors.primary,
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = CaretRight,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = AyatQuTheme.colors.primary,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(
                            if (isLastRead) AyatQuTheme.colors.primary
                            else AyatQuTheme.colors.selectedSurface
                        )
                        .clickable(onClick = onSetLastRead),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(
                            if (isLastRead) R.drawable.bookmark_solid_rounded
                            else R.drawable.bookmark_stroke_rounded
                        ),
                        contentDescription = stringResource(R.string.set_last_read),
                        tint = if (isLastRead) AyatQuTheme.colors.onPrimary
                        else AyatQuTheme.colors.primary,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
                AudioButton(
                    isPlaying = isPlaying,
                    isPreparingAudio = isPreparingAudio,
                    isDownloaded = isDownloaded,
                    isDownloading = isDownloading,
                    onPlayClick = onPlayClick,
                    onDownloadClick = onDownloadClick,
                )
            }
        }
    }
}

@Composable
private fun AudioButton(
    isPlaying: Boolean,
    isPreparingAudio: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    val buttonColor = when {
        isPlaying -> AyatQuTheme.colors.accentGold
        isDownloaded -> AyatQuTheme.colors.primary
        else -> AyatQuTheme.colors.selectedSurface
    }
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape).background(buttonColor)
            .then(
                if (!isDownloading) Modifier.clickable {
                    if (isDownloaded) onPlayClick() else onDownloadClick()
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isDownloading || isPreparingAudio -> CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = if (isPreparingAudio) AyatQuTheme.colors.onPrimary
                else AyatQuTheme.colors.primary,
                strokeWidth = 2.dp,
            )
            isPlaying -> Icon(
                imageVector = Pause,
                contentDescription = stringResource(R.string.pause),
                tint = AyatQuTheme.colors.onPrimary,
                modifier = Modifier.size(16.dp),
            )
            isDownloaded -> Icon(
                imageVector = Play,
                contentDescription = stringResource(R.string.play),
                tint = AyatQuTheme.colors.onPrimary,
                modifier = Modifier.size(16.dp),
            )
            else -> Icon(
                imageVector = Download,
                contentDescription = stringResource(R.string.download),
                tint = AyatQuTheme.colors.primary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun JuzError(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(R.string.couldnt_load_juz),
            fontWeight = FontWeight.Bold,
            color = AyatQuTheme.colors.textPrimary,
        )
        if (message.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(message, textAlign = TextAlign.Center, color = AyatQuTheme.colors.textMuted)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onRetry) { Text(stringResource(R.string.try_again)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewJuzDetail() {
    val verse = JuzVerse(
        surahNumber = 1,
        surahName = "Al-Fatihah",
        ayahNumber = 1,
        verseKey = "1:1",
        arabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
        transliteration = "Bismi Allahi arrahmani arraheem",
        translations = mapOf("en" to "In the name of Allah, the Entirely Merciful."),
    )
    AyatQuTheme {
        JuzDetailScreen(
            state = JuzDetailState(
                juzNumber = 1,
                juz = Juz(1, 148, listOf(verse)),
                downloadedVerseKeys = setOf("1:1"),
                currentLastRead = LastRead(1, 1, "Al-Fatihah"),
            ),
            onEvent = {},
        )
    }
}
