package br.com.carbuapp.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.dashboard.data.DashboardRepositoryImpl
import br.com.carbuapp.dashboard.domain.DashboardSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<DashboardSummary>>(UiState.Loading)
    val uiState: StateFlow<UiState<DashboardSummary>> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getSummary()
            _uiState.value = if (result.isSuccess) UiState.Success(result.getOrThrow())
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar dashboard")
        }
    }
}
