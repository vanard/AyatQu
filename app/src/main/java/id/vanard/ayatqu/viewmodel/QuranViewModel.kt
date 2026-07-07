package id.vanard.ayatqu.viewmodel

import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class QuranUiState(
    val isLoading: Boolean = false,
    val surahs: List<Surah> = emptyList(),
    val query: String = "",
    val error: String? = null,
    val pageSize: Int = 20,
) {
    val filteredSurahs: List<Surah>
        get() = if (query.isBlank()) surahs
        else surahs.filter { matches(it, query.trim()) }

    private fun matches(s: Surah, q: String): Boolean {
        val needle = q.lowercase()
        return s.nameEnglish.lowercase().contains(needle) ||
            s.nameTranslation.lowercase().contains(needle) ||
            s.nameArabic.contains(q) ||
            s.number.toString() == needle
    }
}

// ── Side effects ──────────────────────────────────────────────────────────────

sealed class QuranEvent {
    data class NavigateToSurah(val surahNumber: Int) : QuranEvent()
    data class ShowError(val message: String) : QuranEvent()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class QuranViewModel(
    private val repository: QuranRepository,
    networkUtils: NetworkUtils,
) : NetworkAwareViewModel(networkUtils) {

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        loadSurahs()
    }

    fun loadSurahs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getSurahs()
                .onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, surahs = list) }
                }
                .onFailure { e ->
                    val msg = e.message ?: "Failed to load surahs"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                }
        }
    }

    fun onQueryChange(q: String) {
        _uiState.update { it.copy(query = q) }
    }

    fun clearQuery() {
        _uiState.update { it.copy(query = "") }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
