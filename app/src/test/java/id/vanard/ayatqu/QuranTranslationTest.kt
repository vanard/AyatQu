package id.vanard.ayatqu

import id.vanard.ayatqu.presentation.quran.translationFor
import org.junit.Assert.assertEquals
import org.junit.Test

class QuranTranslationTest {
    private val translations = mapOf(
        "en" to "In the name of Allah.",
        "id" to "Dengan nama Allah.",
    )

    @Test
    fun englishUsesSahihInternationalTranslation() {
        assertEquals("In the name of Allah.", translations.translationFor("en"))
    }

    @Test
    fun bahasaIndonesiaUsesIndonesianTranslation() {
        assertEquals("Dengan nama Allah.", translations.translationFor("id"))
        assertEquals("Dengan nama Allah.", translations.translationFor("in"))
    }

    @Test
    fun missingIndonesianTranslationFallsBackToSahihInternational() {
        assertEquals(
            "In the name of Allah.",
            mapOf("en" to "In the name of Allah.").translationFor("id"),
        )
    }
}
