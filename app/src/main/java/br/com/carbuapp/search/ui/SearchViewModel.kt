package br.com.carbuapp.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.search.domain.SearchRepository
import br.com.carbuapp.search.domain.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<SearchResult>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<SearchResult>>> = _uiState.asStateFlow()

    init {
        @OptIn(FlowPreview::class)
        _query
            .debounce(400)
            .distinctUntilChanged()
            .filter { it.length >= 2 }
            .onEach { doSearch(it) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) {
        _query.value = value
        if (value.length < 2) _uiState.value = UiState.Idle
    }

    private fun doSearch(q: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val results = repository.search(q)
                _uiState.value = if (results.isEmpty()) UiState.Empty else UiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun clearQuery() {
        _query.value = ""
        _uiState.value = UiState.Idle
    }
}
