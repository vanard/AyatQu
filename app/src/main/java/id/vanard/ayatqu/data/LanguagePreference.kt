package id.vanard.ayatqu.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class LanguagePreference {

    fun getCurrentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) return LANGUAGE_ENGLISH
        return locales[0]?.language ?: LANGUAGE_ENGLISH
    }

    fun setLanguage(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_INDONESIAN = "id"
    }
}
