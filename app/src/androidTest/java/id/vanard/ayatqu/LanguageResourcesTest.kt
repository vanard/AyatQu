package id.vanard.ayatqu

import android.app.LocaleManager
import android.content.res.Configuration
import android.os.LocaleList
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.filters.SdkSuppress
import id.vanard.ayatqu.data.LanguagePreference
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LanguageResourcesTest {
    @Test
    @SdkSuppress(minSdkVersion = 33)
    fun unsetLanguageDefaultsToEnglishAndSavedIndonesianIsPreserved() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val localeManager = context.getSystemService(LocaleManager::class.java)
        val originalLocales = localeManager.applicationLocales
        // Simulate resources inherited from an Indonesian device without changing the device.
        val configuration = Configuration(context.resources.configuration).apply {
            setLocales(LocaleList.forLanguageTags("id"))
        }
        val preference = LanguagePreference(context.createConfigurationContext(configuration))
        try {
            localeManager.applicationLocales = LocaleList.getEmptyLocaleList()
            assertEquals("en", preference.getCurrentLanguageCode())
            preference.applySavedLanguageOrDefault()
            assertEquals("en", localeManager.applicationLocales.toLanguageTags())

            preference.setLanguage("id")
            val restoredPreference = LanguagePreference(context)
            restoredPreference.applySavedLanguageOrDefault()
            assertEquals("id", restoredPreference.getCurrentLanguageCode())
            assertEquals("id", localeManager.applicationLocales.toLanguageTags())
        } finally {
            localeManager.applicationLocales = originalLocales
        }
    }

    @Test
    fun selectedLanguageResolvesTranslatedResourcesInBothDirections() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for ((language, expected) in listOf("en" to "Settings", "id" to "Pengaturan", "en" to "Settings")) {
            val configuration = Configuration(context.resources.configuration).apply {
                setLocales(LocaleList.forLanguageTags(language))
            }
            val localizedContext = context.createConfigurationContext(configuration)
            assertEquals("Resources for $language", expected, localizedContext.getString(R.string.settings))
        }
    }
}
