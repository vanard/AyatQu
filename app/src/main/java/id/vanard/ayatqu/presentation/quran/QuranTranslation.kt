package id.vanard.ayatqu.presentation.quran

internal const val ENGLISH_TRANSLATION_KEY = "en"
internal const val INDONESIAN_TRANSLATION_KEY = "id"

internal fun Map<String, String>.translationFor(languageCode: String): String {
    val preferredKey = when (languageCode.lowercase()) {
        INDONESIAN_TRANSLATION_KEY, "in" -> INDONESIAN_TRANSLATION_KEY
        else -> ENGLISH_TRANSLATION_KEY
    }

    return this[preferredKey]
        ?.takeIf(String::isNotBlank)
        ?: this[ENGLISH_TRANSLATION_KEY].orEmpty()
}
