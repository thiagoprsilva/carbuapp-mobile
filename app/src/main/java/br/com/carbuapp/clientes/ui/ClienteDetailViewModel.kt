package br.com.carbuapp.clientes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.clientes.domain.model.Cliente
import br.com.carbuapp.clientes.domain.usecase.DeleteClienteUseCase
import br.com.carbuapp.clientes.domain.usecase.GetClienteByIdUseCase
import br.com.carbuapp.core.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getClienteByIdUseCase: GetClienteByIdUseCase,
    private val deleteClienteUseCase: DeleteClienteUseCase
) : ViewModel() {

    private val clienteId: Int = checkNotNull(savedStateHandle["clienteId"])

    private val _clienteState = MutableStateFlow<UiState<Cliente>>(UiState.Loading)
    val clienteState: StateFlow<UiState<Cliente>> = _clienteState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _clienteState.value = UiState.Loading
            val result = getClienteByIdUseCase(clienteId)
            _clienteState.value = if (result.isSuccess)
                UiState.Success(result.getOrThrow())
            else
                UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar cliente")
        }
    }

    fun delete() {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            val result = deleteClienteUseCase(clienteId)
            _deleteState.value = if (result.isSuccess) UiState.Success(Unit)
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao excluir cliente")
        }
    }
}
