package id.vanard.ayatqu.data

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class LanguagePreference(
    private val context: Context,
) {

    fun getCurrentLanguageCode(): String {
        val selectedLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales
                .takeUnless(LocaleList::isEmpty)
                ?.get(0)
                ?.language
        } else {
            AppCompatDelegate.getApplicationLocales()
                .takeUnless(LocaleListCompat::isEmpty)
                ?.get(0)
                ?.language
        }
        // An unset app locale means first launch, not "follow system".
        return normalizeLanguageCode(selectedLanguage)
    }

    fun applySavedLanguageOrDefault() {
        // Also persists an explicit English locale when no selection exists yet.
        setLanguage(getCurrentLanguageCode())
    }

    fun setLanguage(languageCode: String) {
        val normalizedCode = normalizeLanguageCode(languageCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(normalizedCode)
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(normalizedCode)
            )
        }
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_INDONESIAN = "id"

        fun normalizeLanguageCode(languageCode: String?): String = when (languageCode?.lowercase()) {
            LANGUAGE_INDONESIAN, "in" -> LANGUAGE_INDONESIAN
            else -> LANGUAGE_ENGLISH
        }
    }
}
