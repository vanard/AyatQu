package id.vanard.ayatqu

import id.vanard.ayatqu.data.AdhanPreference
import org.junit.Assert.assertEquals
import org.junit.Test

class AdhanPreferenceTest {
    @Test fun missingAndRemovedSoundsResolveToOmarHisham() {
        listOf(null, "adzan_default", "adhan_default", "unknown").forEach {
            assertEquals(AdhanPreference.SOUND_TYPE_OMAR_HISHAM, AdhanPreference.normalizeSoundType(it))
        }
        assertEquals(R.raw.adhan_omar_hisham, AdhanPreference.getRawResId("adzan_default"))
    }

    @Test fun existingChoicesIncludingSilentArePreservedWithoutDuplicateDefault() {
        val options = AdhanPreference.soundTypes
        assertEquals(options.size, options.distinct().size)
        options.forEach { assertEquals(it, AdhanPreference.normalizeSoundType(it)) }
    }
}
