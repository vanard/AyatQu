package id.vanard.ayatqu

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.presentation.profile.ProfileRouter
import id.vanard.ayatqu.presentation.profile.ProfileViewModel
import id.vanard.ayatqu.presentation.profile.contract.ProfileEvent
import id.vanard.ayatqu.presentation.profile.contract.ProfileSideEffect
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.GlobalContext

class AdhanSoundSelectionTest {
    @get:Rule val compose = createComposeRule()
    private val context: Context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun viewModelRequiresPreviewAndCancelDoesNotSave() = runBlocking {
        val prefs = AdhanPreference(context)
        val original = prefs.adhanSoundType.first()
        withContext(Dispatchers.Main) {
            val vm = GlobalContext.get().get<ProfileViewModel>()
            val store = androidx.lifecycle.ViewModelStore().apply { put("test", vm) }
            try {
                withTimeout(5000) { vm.uiState.first { it.adhanSoundType == original } }
                vm.onEvent(ProfileEvent.SoundClicked)
                vm.onEvent(ProfileEvent.SoundConfirmed)
                assertTrue(vm.uiState.value.showSoundDialog)
                assertFalse(vm.uiState.value.soundPreviewReady)
                val effect = async(start = CoroutineStart.UNDISPATCHED) { vm.uiEffect.first() }
                vm.onEvent(ProfileEvent.SoundTypeChanged(AdhanPreference.SOUND_TYPE_OMAR_HISHAM))
                assertEquals(ProfileSideEffect.PreviewSound(AdhanPreference.SOUND_TYPE_OMAR_HISHAM), effect.await())
                assertFalse(vm.uiState.value.soundPreviewReady)
                vm.onEvent(ProfileEvent.SoundPreviewStarted(AdhanPreference.SOUND_TYPE_SUBUH))
                assertFalse(vm.uiState.value.soundPreviewReady)
                vm.onEvent(ProfileEvent.SoundPreviewStarted(AdhanPreference.SOUND_TYPE_OMAR_HISHAM))
                assertTrue(vm.uiState.value.soundPreviewReady)
                vm.onEvent(ProfileEvent.SoundDismissed)
                assertFalse(vm.uiState.value.showSoundDialog)
                assertEquals(original, prefs.adhanSoundType.first())
                vm.onEvent(ProfileEvent.SoundClicked)
                assertNull(vm.uiState.value.pendingSoundType)
                assertFalse(vm.uiState.value.soundPreviewReady)
            } finally { store.clear() }
        }
    }

    @Test fun dialogPreviewsThenSavesOnlyAfterOk() {
        val prefs = AdhanPreference(context)
        val original = runBlocking { prefs.adhanSoundType.first() }
        val enabled = runBlocking { prefs.notificationsEnabled.first() }
        runBlocking { prefs.setNotificationsEnabled(true) }
        try {
            compose.setContent { AyatQuTheme { ProfileRouter() } }
            val soundTitle = context.getString(R.string.adhan_sound)
            compose.onNodeWithText(soundTitle).performScrollTo().performClick()
            compose.onNodeWithText(context.getString(R.string.ok)).assertIsNotEnabled()
            compose.onNodeWithText("Omar Hisham Al Arabi").performClick()
            compose.waitUntil(5000) {
                compose.onAllNodesWithText(context.getString(R.string.ok))
                    .filter(isEnabled()).fetchSemanticsNodes().isNotEmpty()
            }
            assertEquals(original, runBlocking { prefs.adhanSoundType.first() })
            compose.onNodeWithText(context.getString(R.string.cancel)).performClick()
            assertEquals(original, runBlocking { prefs.adhanSoundType.first() })
            compose.onNodeWithText(soundTitle).performClick()
            compose.onNodeWithText(context.getString(R.string.ok)).assertIsNotEnabled()
            compose.onNodeWithText(context.getString(R.string.silent)).performClick()
            compose.onNodeWithText(context.getString(R.string.ok)).assertIsEnabled().performClick()
            runBlocking { withTimeout(5000) { prefs.adhanSoundType.first { it == AdhanPreference.SOUND_TYPE_SILENT } } }
        } finally {
            runBlocking { prefs.setAdhanSoundType(original); prefs.setNotificationsEnabled(enabled) }
        }
    }
}
