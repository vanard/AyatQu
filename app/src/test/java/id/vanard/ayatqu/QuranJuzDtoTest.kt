package id.vanard.ayatqu

import com.google.gson.Gson
import id.vanard.ayatqu.data.remote.dto.JuzResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranJuzDtoTest {
    @Test
    fun parsesJuzResponseFromUmmahApiSchema() {
        val response = Gson().fromJson(
            """
            {
              "success": true,
              "data": {
                "juz_number": 1,
                "total_verses": 148,
                "verses": [
                  {
                    "verse_key": "1:1",
                    "surah_name": "Al-Fatihah",
                    "ayah": 1,
                    "arabic": "بِسْمِ ٱللَّهِ",
                    "transliteration": "Bismi Allahi",
                    "translations": {
                      "sahih_international": "In the name of Allah.",
                      "indonesian": "Dengan nama Allah."
                    }
                  }
                ]
              }
            }
            """.trimIndent(),
            JuzResponse::class.java,
        )

        assertTrue(response.success)
        assertEquals(1, response.data.juzNumber)
        assertEquals(148, response.data.totalVerses)
        assertEquals("1:1", response.data.verses.single().verseKey)
        assertEquals("Dengan nama Allah.", response.data.verses.single().translations?.indonesian)
    }
}
