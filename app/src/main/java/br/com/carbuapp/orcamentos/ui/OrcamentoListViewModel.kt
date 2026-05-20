package br.com.carbuapp.orcamentos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.orcamentos.domain.model.Orcamento
import br.com.carbuapp.orcamentos.domain.usecase.GetOrcamentosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrcamentoListViewModel @Inject constructor(
    private val getOrcamentos: GetOrcamentosUseCase
) : ViewModel() {

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val orcamentos: StateFlow<List<Orcamento>> = _statusFilter
        .flatMapLatest { status ->
            if (status == null) getOrcamentos.observeAll()
            else getOrcamentos.observeByStatus(status)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _refreshState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val refreshState: StateFlow<UiState<Unit>> = _refreshState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _refreshState.value = UiState.Loading
            val result = getOrcamentos.refresh(status = _statusFilter.value)
            _refreshState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Erro ao carregar orçamentos") }
            )
        }
    }

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
        refresh()
    }
}
