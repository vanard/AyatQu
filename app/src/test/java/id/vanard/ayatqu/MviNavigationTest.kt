package id.vanard.ayatqu

import id.vanard.ayatqu.core.navigation.AppNavigationCommand
import id.vanard.ayatqu.data.LanguagePreference
import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.navigation.directions.AuthDirection
import id.vanard.ayatqu.navigation.directions.QuranDirection
import id.vanard.ayatqu.navigation.routes.MainRoute
import id.vanard.ayatqu.navigation.routes.QuranRoute
import id.vanard.ayatqu.presentation.quran.list.contract.QuranState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MviNavigationTest {
    private val surahs = listOf(
        Surah(
            number = 1,
            nameArabic = "الفاتحة",
            nameEnglish = "Al-Fatihah",
            nameTranslation = "The Opening",
            revelationPlace = "Mecca",
            versesCount = 7,
            bismillahPre = true,
        ),
        Surah(
            number = 2,
            nameArabic = "البقرة",
            nameEnglish = "Al-Baqarah",
            nameTranslation = "The Cow",
            revelationPlace = "Medina",
            versesCount = 286,
            bismillahPre = true,
        ),
    )

    @Test
    fun languageCodesResolveToSupportedResourceQualifiers() {
        assertEquals(
            LanguagePreference.LANGUAGE_ENGLISH,
            LanguagePreference.normalizeLanguageCode("en"),
        )
        assertEquals(
            LanguagePreference.LANGUAGE_INDONESIAN,
            LanguagePreference.normalizeLanguageCode("id"),
        )
        assertEquals(
            LanguagePreference.LANGUAGE_INDONESIAN,
            LanguagePreference.normalizeLanguageCode("in"),
        )
        assertEquals(
            LanguagePreference.LANGUAGE_ENGLISH,
            LanguagePreference.normalizeLanguageCode("fr"),
        )
    }

    @Test
    fun quranStateFiltersByNameTranslationArabicAndNumber() {
        assertEquals(listOf(surahs[0]), QuranState(surahs = surahs, query = "fatihah").filteredSurahs)
        assertEquals(listOf(surahs[1]), QuranState(surahs = surahs, query = "cow").filteredSurahs)
        assertEquals(listOf(surahs[1]), QuranState(surahs = surahs, query = "البقرة").filteredSurahs)
        assertEquals(listOf(surahs[0]), QuranState(surahs = surahs, query = " 1 ").filteredSurahs)
    }

    @Test
    fun quranDetailDirectionCarriesTypedArguments() {
        val command: AppNavigationCommand = QuranDirection.detail(36, 12)

        assertTrue(command is AppNavigationCommand.Navigate)
        assertEquals(
            QuranRoute.Detail(surahNumber = 36, ayahNumber = 12),
            (command as AppNavigationCommand.Navigate).route,
        )
    }

    @Test
    fun juzDirectionCarriesTypedArgument() {
        val command: AppNavigationCommand = QuranDirection.juz(30)

        assertTrue(command is AppNavigationCommand.Navigate)
        assertEquals(
            QuranRoute.JuzDetail(juzNumber = 30),
            (command as AppNavigationCommand.Navigate).route,
        )
    }

    @Test
    fun quranStateFiltersJuzByNumberAndBoundarySurah() {
        assertEquals(listOf(30), QuranState(query = "30").filteredJuzs.map { it.number })
        assertEquals(listOf(1), QuranState(query = "fatihah").filteredJuzs.map { it.number })
        assertEquals(listOf(30), QuranState(query = "nas").filteredJuzs.map { it.number })
    }

    @Test
    fun authenticatedHomeDirectionClearsThePreviousFlow() {
        val command: AppNavigationCommand = AuthDirection.home

        assertTrue(command is AppNavigationCommand.ReplaceAll)
        assertEquals(MainRoute.Home, (command as AppNavigationCommand.ReplaceAll).route)
    }
}
