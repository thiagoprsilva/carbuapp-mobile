package br.com.carbuapp.veiculos.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.veiculos.domain.model.Veiculo
import br.com.carbuapp.veiculos.domain.usecase.DeleteVeiculoUseCase
import br.com.carbuapp.veiculos.domain.usecase.GetVeiculosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VeiculoListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getVeiculosUseCase: GetVeiculosUseCase,
    private val deleteVeiculoUseCase: DeleteVeiculoUseCase
) : ViewModel() {

    // clienteId opcional — quando vem da tela de detalhe de cliente
    val clienteId: Int? = savedStateHandle.get<Int>("clienteId")?.takeIf { it != -1 }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val veiculos: StateFlow<List<Veiculo>> = _searchQuery
        .flatMapLatest { query ->
            when {
                query.isNotBlank() -> getVeiculosUseCase.search(query)
                clienteId != null  -> getVeiculosUseCase.byCliente(clienteId)
                else               -> getVeiculosUseCase()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _refreshState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val refreshState: StateFlow<UiState<Unit>> = _refreshState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _refreshState.value = UiState.Loading
            val result = getVeiculosUseCase.refresh(clienteId)
            _refreshState.value = if (result.isSuccess) UiState.Success(Unit)
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar veículos")
        }
    }

    fun onSearchChange(query: String) { _searchQuery.value = query }

    fun delete(veiculoId: Int) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            val result = deleteVeiculoUseCase(veiculoId)
            _deleteState.value = if (result.isSuccess) UiState.Success(Unit)
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao excluir veículo")
        }
    }

    fun clearDeleteState() { _deleteState.value = UiState.Idle }
}
