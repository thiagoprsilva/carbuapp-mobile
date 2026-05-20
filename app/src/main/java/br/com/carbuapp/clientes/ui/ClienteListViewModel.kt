package br.com.carbuapp.clientes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.clientes.domain.model.Cliente
import br.com.carbuapp.clientes.domain.usecase.DeleteClienteUseCase
import br.com.carbuapp.clientes.domain.usecase.GetClientesUseCase
import br.com.carbuapp.core.util.UiState
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
class ClienteListViewModel @Inject constructor(
    private val getClientesUseCase: GetClientesUseCase,
    private val deleteClienteUseCase: DeleteClienteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val clientes: StateFlow<List<Cliente>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) getClientesUseCase()
            else getClientesUseCase.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _refreshState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val refreshState: StateFlow<UiState<Unit>> = _refreshState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshState.value = UiState.Loading
            val result = getClientesUseCase.refresh()
            _refreshState.value = if (result.isSuccess) UiState.Success(Unit)
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar clientes")
        }
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun delete(clienteId: Int) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            val result = deleteClienteUseCase(clienteId)
            _deleteState.value = if (result.isSuccess) UiState.Success(Unit)
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao excluir cliente")
        }
    }

    fun clearDeleteState() {
        _deleteState.value = UiState.Idle
    }
}
