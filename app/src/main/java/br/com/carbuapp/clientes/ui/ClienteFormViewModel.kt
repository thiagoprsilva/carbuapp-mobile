package br.com.carbuapp.clientes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.clientes.domain.usecase.GetClienteByIdUseCase
import br.com.carbuapp.clientes.domain.usecase.SaveClienteUseCase
import br.com.carbuapp.core.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getClienteByIdUseCase: GetClienteByIdUseCase,
    private val saveClienteUseCase: SaveClienteUseCase
) : ViewModel() {

    private val clienteId: Int? = savedStateHandle.get<Int>("clienteId")?.takeIf { it != -1 }
    val isEditing: Boolean = clienteId != null

    val nome = MutableStateFlow("")
    val telefone = MutableStateFlow("")

    private val _loadState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loadState: StateFlow<UiState<Unit>> = _loadState.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val saveState: StateFlow<UiState<Unit>> = _saveState.asStateFlow()

    init {
        if (isEditing) loadExisting()
    }

    private fun loadExisting() {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            val result = getClienteByIdUseCase(clienteId!!)
            if (result.isSuccess) {
                val cliente = result.getOrThrow()
                nome.value = cliente.nome
                telefone.value = cliente.telefone ?: ""
                _loadState.value = UiState.Success(Unit)
            } else {
                _loadState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar")
            }
        }
    }

    fun save() {
        val nomeTrimmed = nome.value.trim()
        val telefoneTrimmed = telefone.value.trim().ifBlank { null }

        if (nomeTrimmed.isBlank()) {
            _saveState.value = UiState.Error("O nome é obrigatório")
            return
        }

        viewModelScope.launch {
            _saveState.value = UiState.Loading
            val result = if (isEditing)
                saveClienteUseCase.update(clienteId!!, nomeTrimmed, telefoneTrimmed)
            else
                saveClienteUseCase.create(nomeTrimmed, telefoneTrimmed)

            _saveState.value = if (result.isSuccess) UiState.Success(Unit)
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao salvar")
        }
    }
}
