package id.vanard.ayatqu

import id.vanard.ayatqu.util.LocationHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationHelperTest {
    @Test
    fun removesIndonesianAdministrativePrefixes() {
        mapOf(
            "Kecamatan Tanjung Priok" to "Tanjung Priok",
            "Kelurahan Sunter Agung" to "Sunter Agung",
            "Kota Jakarta Utara" to "Jakarta Utara",
            "Kota Administrasi Jakarta Selatan" to "Jakarta Selatan",
            "Provinsi DKI Jakarta" to "DKI Jakarta",
            "Kabupaten Badung" to "Badung",
            "Desa Ubud" to "Ubud",
        ).forEach { (input, expected) ->
            assertEquals(expected, LocationHelper.cleanLocationName(input))
        }
    }

    @Test
    fun cleaningIsCaseInsensitiveAndNormalizesWhitespace() {
        assertEquals("Tanjung Priok", LocationHelper.cleanLocationName("  kecamatan   Tanjung Priok  "))
    }

    @Test
    fun keepsNamesWithoutAdministrativePrefix() {
        assertEquals("Tanjung Priok", LocationHelper.cleanLocationName("Tanjung Priok"))
        assertEquals("Kota", LocationHelper.cleanLocationName("Kota"))
        assertEquals("-6.12, 106.88", LocationHelper.cleanLocationName("-6.12, 106.88"))
    }
}
