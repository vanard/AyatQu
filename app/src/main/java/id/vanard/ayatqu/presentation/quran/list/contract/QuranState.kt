package id.vanard.ayatqu.presentation.quran.list.contract

import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.domain.model.JuzCatalog
import id.vanard.ayatqu.domain.model.JuzSummary

data class QuranState(
    val isLoading: Boolean = false,
    val surahs: List<Surah> = emptyList(),
    val query: String = "",
    val errorMessage: String? = null,
    val pageSize: Int = 20,
    val selectedTab: Int = 0,
    val isNetworkAvailable: Boolean = true,
    val juzs: List<JuzSummary> = JuzCatalog.items,
) {
    val filteredSurahs: List<Surah>
        get() = if (query.isBlank()) surahs else surahs.filter { surah ->
            val needle = query.trim().lowercase()
            surah.nameEnglish.lowercase().contains(needle) ||
                surah.nameTranslation.lowercase().contains(needle) ||
                surah.nameArabic.contains(query.trim()) ||
                surah.number.toString() == needle
        }

    val filteredJuzs: List<JuzSummary>
        get() = if (query.isBlank()) juzs else juzs.filter { juz ->
            val needle = query.trim().lowercase()
            juz.number.toString() == needle ||
                juz.startSurahName.lowercase().contains(needle) ||
                juz.endSurahName.lowercase().contains(needle)
        }
}
