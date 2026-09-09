package id.vanard.ayatqu.presentation.quran.list.contract

import id.vanard.ayatqu.domain.model.Surah

data class QuranState(
    val isLoading: Boolean = false,
    val surahs: List<Surah> = emptyList(),
    val query: String = "",
    val errorMessage: String? = null,
    val pageSize: Int = 20,
    val selectedTab: Int = 0,
    val isNetworkAvailable: Boolean = true,
) {
    val filteredSurahs: List<Surah>
        get() = if (query.isBlank()) surahs else surahs.filter { surah ->
            val needle = query.trim().lowercase()
            surah.nameEnglish.lowercase().contains(needle) ||
                surah.nameTranslation.lowercase().contains(needle) ||
                surah.nameArabic.contains(query.trim()) ||
                surah.number.toString() == needle
        }
}
