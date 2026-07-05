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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme

// ── Design tokens ─────────────────────────────────────────────────────────────
private val ColorTextPrimary  = Color(0xFF2D2D2D)
private val ColorGold         = Color(0xFFC19A6B)
private val ColorMuted        = Color(0xFF8E8E93)
private val ColorBorder       = Color(0xFFF9FAFB)
private val ColorTabBg        = Color(0x80F0E6D2)  // 50% opacity
private val ColorCardGradient = listOf(Color(0xFFF5E6CC), Color(0xFFF8F1E4), Color(0xFFFFFFFF))
private val ColorCardBorder   = Color(0xFFEBE3D5)

// ── Mock data ─────────────────────────────────────────────────────────────────
private data class SurahItem(
    val number: Int,
    val latinName: String,
    val arabicName: String,
    val ayahCount: Int,
)

private val mockSurahs = listOf(
    SurahItem(1,  "Al Fatihah",  "الفاتحة", 7),
    SurahItem(2,  "Al Baqarah",  "البقرة",  286),
    SurahItem(3,  "Ali Imran",   "آل عمران", 200),
    SurahItem(4,  "An Nisa",     "النساء",   176),
    SurahItem(5,  "Al Maidah",   "المائدة",  120),
    SurahItem(6,  "Al An'am",    "الأنعام",  165),
    SurahItem(7,  "Al A'raf",    "الأعراف",  206),
    SurahItem(8,  "Al Anfal",    "الأنفال",  75),
    SurahItem(9,  "At Taubah",   "التوبة",   129),
    SurahItem(10, "Yunus",       "يونس",     109),
)

private val tabs = listOf("Surah", "Juz", "Page")

@Composable
fun HomeScreen(
    onSurahClick: (Int) -> Unit = {},
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        HomeHeader()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            // ── Last Read Card ────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(12.dp))
                LastReadCard(
                    surahName = "Al-Fatihah",
                    ayahNumber = 1
                )
                Spacer(Modifier.height(24.dp))
            }

            // ── Al Quran section header + tabs ────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Al Quran",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextPrimary
                    )
                    SurahTabRow(
                        tabs = tabs,
                        selected = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Surah list ────────────────────────────────────────────────────
            items(mockSurahs) { surah ->
                SurahListItem(
                    surah = surah,
                    onClick = { onSurahClick(surah.number) }
                )
                HorizontalDivider(color = ColorBorder, thickness = 1.dp)
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Menu icon
        IconPlaceholder(size = 32)

        // Title
        Text(
            text = "QURAN",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary,
            letterSpacing = 1.8.sp
        )

        // Search icon
        IconPlaceholder(size = 32)
    }
}

// ── Last Read Card ────────────────────────────────────────────────────────────

@Composable
private fun LastReadCard(surahName: String, ayahNumber: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(ColorCardGradient))
            .border(1.dp, ColorCardBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 25.dp)
    ) {
        // Decorative mosque silhouette placeholder — right side
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 160.dp, height = 110.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ColorGold.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text("🕌", fontSize = 48.sp) // TODO: replace with actual mosque SVG asset
        }

        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(vertical = 20.dp)
        ) {
            Text(
                text = "Last Read",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ColorGold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = surahName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "AYAH NO: $ayahNumber",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = ColorMuted,
                letterSpacing = 0.6.sp
            )
        }
    }
}

// ── Surah / Juz / Page tab row ────────────────────────────────────────────────

@Composable
private fun SurahTabRow(
    tabs: List<String>,
    selected: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(ColorTabBg)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = index == selected
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) ColorGold else ColorMuted
                )
            }
        }
    }
}

// ── Surah list item ───────────────────────────────────────────────────────────

@Composable
private fun SurahListItem(surah: SurahItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Number badge
        SurahNumberBadge(number = surah.number)

        Spacer(Modifier.width(16.dp))

        // Latin name + ayah count
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = surah.latinName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${surah.ayahCount} AYAHS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = ColorMuted,
                letterSpacing = (-0.5).sp
            )
        }

        // Arabic name
        Text(
            text = surah.arabicName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = ColorGold,
            textAlign = TextAlign.End
        )
    }
}

// ── Surah number badge ────────────────────────────────────────────────────────

@Composable
private fun SurahNumberBadge(number: Int) {
    Box(
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        // Octagon-ish background — using a circle as placeholder
        // TODO: replace with actual diamond/octagon SVG asset from Figma
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGold.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString().padStart(2, '0'),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary
            )
        }
    }
}

// ── Icon placeholder ──────────────────────────────────────────────────────────

@Composable
private fun IconPlaceholder(size: Int) {
    // TODO: replace with actual SVG icon assets
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ColorBorder)
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Home - Light")
@Composable
private fun PreviewHomeLight() {
    AyatQuTheme(darkTheme = false) {
        HomeScreen()
    }
}

@Preview(showBackground = true, name = "Home - Dark")
@Composable
private fun PreviewHomeDark() {
    AyatQuTheme(darkTheme = true) {
        HomeScreen()
    }
}
