package id.vanard.ayatqu.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LanguagePreference(private val context: Context) {

    private val dataStore = context.dataStore

    private val languageKey = stringPreferencesKey("app_language")

    val languageCode: Flow<String> = dataStore.data
        .map { prefs -> prefs[languageKey] ?: LANGUAGE_ENGLISH }

    fun getCurrentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) return LANGUAGE_ENGLISH
        return locales[0]?.language ?: LANGUAGE_ENGLISH
    }

    suspend fun setLanguage(languageCode: String) {
        dataStore.edit { prefs ->
            prefs[languageKey] = languageCode
        }
        applyLocale(languageCode)
    }

    fun applySavedLocale() {
        val locales = AppCompatDelegate.getApplicationLocales()
        val current = if (locales.isEmpty) LANGUAGE_ENGLISH else locales[0]?.language ?: LANGUAGE_ENGLISH
        applyLocale(current)
    }

    private fun applyLocale(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_INDONESIAN = "id"
    }
}
