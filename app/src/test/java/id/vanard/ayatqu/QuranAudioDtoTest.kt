package id.vanard.ayatqu

import com.google.gson.Gson
import id.vanard.ayatqu.data.remote.dto.AudioResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class QuranAudioDtoTest {
    private val gson = Gson()

    @Test
    fun parsesAudioUrlFromCurrentRecitersPayload() {
        val response = gson.fromJson(
            """
            {
              "success": true,
              "data": {
                "surah": {
                  "number": 1,
                  "name_arabic": "الفاتحة",
                  "name_english": "Al-Fatihah"
                },
                "verse_key": "1:1",
                "reciters": [
                  {
                    "id": 1,
                    "name": "Mishary Rashid Alafasy",
                    "name_arabic": "مشاري راشد العفاسي",
                    "style": "Murattal",
                    "audio_url": "https://everyayah.com/data/Alafasy_128kbps/001001.mp3"
                  }
                ]
              }
            }
            """.trimIndent(),
            AudioResponse::class.java,
        )

        assertEquals(
            "https://everyayah.com/data/Alafasy_128kbps/001001.mp3",
            response.data.firstAvailableAudioUrl(),
        )
    }

    @Test
    fun retainsSupportForLegacyDirectAudioPayload() {
        val response = gson.fromJson(
            """
            {
              "success": true,
              "data": {
                "surah": 1,
                "ayah": 1,
                "audio": "https://example.com/001001.mp3",
                "reciter": "Example Reciter"
              }
            }
            """.trimIndent(),
            AudioResponse::class.java,
        )

        assertEquals(
            "https://example.com/001001.mp3",
            response.data.firstAvailableAudioUrl(),
        )
    }
}
