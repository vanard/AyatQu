package id.vanard.ayatqu.presentation.quran.list

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.presentation.common.component.NoConnectionView
import id.vanard.ayatqu.core.ui.icon.CaretRight
import id.vanard.ayatqu.core.ui.icon.MagnifyingGlass
import id.vanard.ayatqu.core.ui.icon.X
import id.vanard.ayatqu.presentation.quran.list.contract.OnQuranEvent
import id.vanard.ayatqu.presentation.quran.list.contract.QuranEvent
import id.vanard.ayatqu.presentation.quran.list.contract.QuranState

private val quranTabs = listOf(R.string.tab_surah, R.string.tab_juz)

@Composable
fun QuranScreen(
    state: QuranState,
    onEvent: OnQuranEvent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AyatQuTheme.colors.background)
            .statusBarsPadding(),
    ) {
        QuranHeader(
            query = state.query,
            onQueryChange = { onEvent(QuranEvent.QueryChanged(it)) },
            onClearQuery = { onEvent(QuranEvent.ClearQueryClicked) },
            selectedTab = state.selectedTab,
            onTabSelected = { onEvent(QuranEvent.TabSelected(it)) },
        )

        Spacer(Modifier.height(8.dp))

        when {
            // No network and no cached data → show no-connection screen
            !state.isNetworkAvailable && state.surahs.isEmpty() ->
                NoConnectionView(onRetry = { onEvent(QuranEvent.RetryClicked) })
            state.isLoading && state.surahs.isEmpty() -> LoadingState()
            state.errorMessage != null && state.surahs.isEmpty() ->
                ErrorState(
                    message = state.errorMessage,
                    onRetry = { onEvent(QuranEvent.RetryClicked) },
                )
            state.selectedTab == 0 -> {
                // Surah tab
                if (state.filteredSurahs.isEmpty() && state.query.isNotBlank()) {
                    EmptySearchState(query = state.query)
                } else {
                    SurahList(
                        surahs = state.filteredSurahs,
                        onSurahClick = { onEvent(QuranEvent.SurahClicked(it)) },
                        pageSize = state.pageSize,
                    )
                }
            }
            state.selectedTab == 1 -> {
                // Juz tab
                val filteredJuz = if (state.query.isBlank()) juzData
                else juzData.filter {
                    it.surahName.lowercase().contains(state.query.trim().lowercase()) ||
                        it.number.toString() == state.query.trim()
                }
                if (filteredJuz.isEmpty() && state.query.isNotBlank()) {
                    EmptySearchState(query = state.query)
                } else {
                    JuzList(juzItems = filteredJuz)
                }
            }
        }
    }
}

// ── Header with search and tabs ───────────────────────────────────────────────

@Composable
private fun QuranHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AyatQuTheme.colors.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.al_quran),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AyatQuTheme.colors.primary,
                letterSpacing = 1.5.sp,
            )
            QuranTabRow(
                tabs = quranTabs,
                selected = selectedTab,
                onTabSelected = onTabSelected,
            )
        }

        Spacer(Modifier.height(16.dp))

        // Search bar (show for both tabs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AyatQuTheme.colors.surfaceVariant)
                .border(1.dp, AyatQuTheme.colors.border, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = MagnifyingGlass,
                contentDescription = null,
                tint = AyatQuTheme.colors.textMuted,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = if (selectedTab == 0) stringResource(R.string.search_surah_hint)
                        else stringResource(R.string.search_juz_hint),
                        fontSize = 14.sp,
                        color = AyatQuTheme.colors.textMuted,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = AyatQuTheme.colors.textPrimary,
                        fontWeight = FontWeight.Medium,
                    ),
                    cursorBrush = SolidColor(AyatQuTheme.colors.primary),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClearQuery),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = X,
                        contentDescription = stringResource(R.string.clear),
                        tint = AyatQuTheme.colors.textMuted,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

// ── Surah / Juz tab row ───────────────────────────────────────────────────────

@Composable
private fun QuranTabRow(
    tabs: List<Int>,
    selected: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(AyatQuTheme.colors.surfaceVariant)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { index, labelRes ->
            val isSelected = index == selected
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) AyatQuTheme.colors.surface else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(labelRes),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) AyatQuTheme.colors.accentGold else AyatQuTheme.colors.textMuted,
                )
            }
        }
    }
}

// ── List with auto-load pagination ────────────────────────────────────────────

@Composable
private fun SurahList(
    surahs: List<Surah>,
    onSurahClick: (Int) -> Unit,
    pageSize: Int = 10,
) {
    // Progressive client-side reveal: all data is already loaded,
    // we just show `pageSize` more items each time the user scrolls to the end.
    val displayedCount = remember { mutableStateOf(pageSize) }

    // Reset displayed count when the list changes (e.g. after a search)
    LaunchedEffect(surahs) {
        displayedCount.value = pageSize
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val displayedSurahs = surahs.take(displayedCount.value)

        items(items = displayedSurahs, key = { it.number }) { surah ->
            SurahRow(surah = surah, onClick = { onSurahClick(surah.number) })
            HorizontalDivider(
                modifier = Modifier.padding(start = 80.dp),
                color = AyatQuTheme.colors.border,
                thickness = 0.5.dp,
            )
        }

        // Auto-reveal trigger: when there are more items to show,
        // this item appears at the bottom and instantly loads the next batch.
        if (displayedCount.value < surahs.size) {
            item {
                LaunchedEffect(displayedCount.value) {
                    displayedCount.value = minOf(
                        displayedCount.value + pageSize,
                        surahs.size
                    )
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun SurahRow(surah: Surah, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AyatQuTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SurahNumberBadge(number = surah.number)

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = surah.nameEnglish,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AyatQuTheme.colors.textPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = surah.nameTranslation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AyatQuTheme.colors.textMuted,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.surah_info, surah.versesCount, surah.revelationPlace.uppercase()),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = AyatQuTheme.colors.primary,
                letterSpacing = 0.6.sp,
            )
        }

        Spacer(Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = surah.nameArabic,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AyatQuTheme.colors.accentGold,
                textAlign = TextAlign.End,
            )
            Spacer(Modifier.height(4.dp))
            Icon(
                imageVector = CaretRight,
                contentDescription = null,
                tint = AyatQuTheme.colors.textMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun SurahNumberBadge(number: Int) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(AyatQuTheme.colors.surfaceVariant)
            .border(1.dp, AyatQuTheme.colors.border, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString().padStart(3, '0'),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AyatQuTheme.colors.primary,
            letterSpacing = 0.3.sp,
        )
    }
}

// ── Juz list ──────────────────────────────────────────────────────────────────

private data class JuzItem(
    val number: Int,
    val surahName: String,
    val ayahRange: String,
)

private val juzData = listOf(
    JuzItem(1,  "Al-Fatihah",  "1:1 – Al-Baqarah 141"),
    JuzItem(2,  "Al-Baqarah",  "142 – Al-Baqarah 252"),
    JuzItem(3,  "Al-Baqarah",  "253 – Ali 'Imran 92"),
    JuzItem(4,  "Ali 'Imran",  "93 – An-Nisa 23"),
    JuzItem(5,  "An-Nisa",     "24 – An-Nisa 147"),
    JuzItem(6,  "An-Nisa",     "148 – Al-Ma'idah 81"),
    JuzItem(7,  "Al-Ma'idah",  "82 – Al-An'am 110"),
    JuzItem(8,  "Al-An'am",    "111 – Al-A'raf 87"),
    JuzItem(9,  "Al-A'raf",    "88 – Al-Anfal 40"),
    JuzItem(10, "Al-Anfal",    "41 – At-Tawbah 92"),
    JuzItem(11, "At-Tawbah",   "93 – Hud 5"),
    JuzItem(12, "Hud",         "6 – Yusuf 52"),
    JuzItem(13, "Yusuf",       "53 – Ibrahim 52"),
    JuzItem(14, "Al-Hijr",     "1 – An-Nahl 128"),
    JuzItem(15, "Al-Isra",     "1 – Al-Kahf 74"),
    JuzItem(16, "Al-Kahf",     "75 – Ta-Ha 135"),
    JuzItem(17, "Al-Anbiya",   "1 – Al-Hajj 78"),
    JuzItem(18, "Al-Mu'minun","1 – Al-Furqan 20"),
    JuzItem(19, "Al-Furqan",   "21 – An-Naml 55"),
    JuzItem(20, "An-Naml",     "56 – Al-'Ankabut 45"),
    JuzItem(21, "Al-'Ankabut", "46 – Al-Ahzab 30"),
    JuzItem(22, "Al-Ahzab",    "31 – Ya-Sin 27"),
    JuzItem(23, "Ya-Sin",      "28 – Az-Zumar 31"),
    JuzItem(24, "Az-Zumar",    "32 – Fussilat 46"),
    JuzItem(25, "Fussilat",    "47 – Al-Jathiyah 37"),
    JuzItem(26, "Al-Ahqaf",    "1 – Adh-Dhariyat 30"),
    JuzItem(27, "Adh-Dhariyat","31 – Al-Hadid 29"),
    JuzItem(28, "Al-Mujadilah","1 – At-Tahrim 12"),
    JuzItem(29, "Al-Mulk",     "1 – Al-Mursalat 50"),
    JuzItem(30, "An-Naba",     "1 – An-Nas 6"),
)

@Composable
private fun JuzList(juzItems: List<JuzItem> = juzData) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = juzItems, key = { it.number }) { juz ->
            JuzRow(juz = juz)
            HorizontalDivider(
                modifier = Modifier.padding(start = 80.dp),
                color = AyatQuTheme.colors.border,
                thickness = 0.5.dp,
            )
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun JuzRow(juz: JuzItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AyatQuTheme.colors.surface)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SurahNumberBadge(number = juz.number)

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.juz_number, juz.number),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AyatQuTheme.colors.textPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = juz.surahName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AyatQuTheme.colors.textMuted,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = juz.ayahRange,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = AyatQuTheme.colors.primary,
                letterSpacing = 0.6.sp,
            )
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            imageVector = CaretRight,
            contentDescription = null,
            tint = AyatQuTheme.colors.textMuted,
            modifier = Modifier.size(16.dp),
        )
    }
}

// ── Loading / Error / Empty states ────────────────────────────────────────────

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = AyatQuTheme.colors.primary,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.couldnt_load_surahs),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AyatQuTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 13.sp,
            color = AyatQuTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AyatQuTheme.colors.primary)
                .clickable(onClick = onRetry)
                .padding(horizontal = 24.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.try_again),
                color = AyatQuTheme.colors.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun EmptySearchState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.no_matches_for, query),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AyatQuTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.try_different_search),
            fontSize = 13.sp,
            color = AyatQuTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val sampleState = QuranState(
    isLoading = false,
    surahs = listOf(
        Surah(1,  "الفاتحة",  "Al-Fatihah",  "The Opening",                "makkah", 7,   true),
        Surah(2,  "البقرة",   "Al-Baqarah",  "The Cow",                    "madinah", 286, true),
        Surah(3,  "آل عمران", "Ali 'Imran",  "The Family of Imran",        "madinah", 200, true),
        Surah(4,  "النساء",   "An-Nisa",     "The Women",                  "madinah", 176, true),
        Surah(5,  "المائدة",  "Al-Ma'idah",  "The Table Spread",           "madinah", 120, true),
        Surah(6,  "الأنعام",  "Al-An'am",    "The Cattle",                 "makkah", 165, true),
        Surah(7,  "الأعراف",  "Al-A'raf",    "The Heights",                "makkah", 206, true),
        Surah(8,  "الأنفال",  "Al-Anfal",    "The Spoils of War",          "madinah", 75, true),
        Surah(9,  "التوبة",   "At-Tawbah",   "The Repentance",             "madinah", 129, true),
        Surah(10, "يونس",     "Yunus",       "Jonah",                      "makkah", 109, true),
        Surah(11, "هود",      "Hud",         "Hud",                        "makkah", 123, true),
        Surah(12, "يوسف",     "Yusuf",       "Joseph",                     "makkah", 111, true),
        Surah(13, "الرعد",    "Ar-Ra'd",     "The Thunder",                "madinah", 43, true),
        Surah(14, "إبراهيم",  "Ibrahim",     "Abraham",                    "makkah", 52, true),
        Surah(15, "الحجر",    "Al-Hijr",     "The Rocky Tract",            "makkah", 99, true),
        Surah(16, "النحل",    "An-Nahl",     "The Bee",                    "makkah", 128, true),
        Surah(17, "الإسراء",  "Al-Isra",     "The Night Journey",          "makkah", 111, true),
        Surah(18, "الكهف",    "Al-Kahf",     "The Cave",                   "makkah", 110, true),
        Surah(19, "مريم",     "Maryam",      "Mary",                       "makkah", 98, true),
        Surah(20, "طه",       "Ta-Ha",       "Ta-Ha",                      "makkah", 135, true),
        Surah(36, "يس",       "Ya-Sin",      "Ya-Sin",                      "makkah", 83, true),
        Surah(55, "الرحمن",   "Ar-Rahman",   "The Most Merciful",          "madinah", 78, true),
        Surah(67, "الملك",    "Al-Mulk",     "The Sovereignty",            "makkah", 30, true),
        Surah(112,"الإخلاص",  "Al-Ikhlas",   "The Sincerity",              "makkah", 4,  true),
        Surah(114,"الناس",    "An-Nas",      "Mankind",                    "makkah", 6,  true),
    ),
    pageSize = 10,
)

@Preview(showBackground = true, name = "Quran - Loaded")
@Composable
private fun QuranScreenLoadedPreview() {
    AyatQuTheme(darkTheme = false) {
        QuranScreen(
            state = sampleState,
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Quran - Loading")
@Composable
private fun QuranScreenLoadingPreview() {
    AyatQuTheme(darkTheme = false) {
        QuranScreen(
            state = QuranState(isLoading = true),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Quran - Error")
@Composable
private fun QuranScreenErrorPreview() {
    AyatQuTheme(darkTheme = false) {
        QuranScreen(
            state = QuranState(errorMessage = "Network error. Check your connection."),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Quran - Empty search")
@Composable
private fun QuranScreenEmptyPreview() {
    AyatQuTheme(darkTheme = false) {
        QuranScreen(
            state = sampleState.copy(query = "zzzz"),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Quran - No connection")
@Composable
private fun QuranScreenNoConnectionPreview() {
    AyatQuTheme(darkTheme = false) {
        QuranScreen(
            state = QuranState(isNetworkAvailable = false),
            onEvent = {},
        )
    }
}
