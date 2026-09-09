package id.vanard.ayatqu.presentation.quran.list

import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel
import id.vanard.ayatqu.presentation.quran.list.contract.QuranEvent
import id.vanard.ayatqu.presentation.quran.list.contract.QuranSideEffect
import id.vanard.ayatqu.presentation.quran.list.contract.QuranState
import id.vanard.ayatqu.util.NetworkUtils
import kotlinx.coroutines.launch

// ── ViewModel ─────────────────────────────────────────────────────────────────

class QuranViewModel(
    private val repository: QuranRepository,
    private val networkUtils: NetworkUtils,
) : BaseMviViewModel<QuranState, QuranEvent, QuranSideEffect>(
    QuranState(isNetworkAvailable = networkUtils.isNetworkAvailable())
) {

    init {
        loadSurahs()
        viewModelScope.launch {
            networkUtils.networkState.collect { available ->
                setState { copy(isNetworkAvailable = available) }
            }
        }
    }

    override fun onEvent(event: QuranEvent) {
        when (event) {
            is QuranEvent.QueryChanged -> setState { copy(query = event.query) }
            is QuranEvent.TabSelected -> setState { copy(selectedTab = event.index.coerceIn(0, 1)) }
            is QuranEvent.SurahClicked -> setEffect(QuranSideEffect.NavigateToSurah(event.surahNumber))
            QuranEvent.ClearQueryClicked -> setState { copy(query = "") }
            QuranEvent.RetryClicked -> loadSurahs()
        }
    }

    private fun loadSurahs() {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            repository.getSurahs()
                .onSuccess { list ->
                    setState { copy(isLoading = false, surahs = list) }
                }
                .onFailure { e ->
                    val msg = e.message ?: "Failed to load surahs"
                    setState { copy(isLoading = false, errorMessage = msg) }
                }
        }
    }
}
