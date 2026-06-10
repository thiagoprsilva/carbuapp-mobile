package br.com.carbuapp.usuarios.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.usuarios.domain.Usuario
import br.com.carbuapp.usuarios.domain.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuariosViewModel @Inject constructor(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Usuario>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Usuario>>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.listar()
            _uiState.value = if (result.isSuccess) {
                val list = result.getOrThrow()
                if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar usuários")
            }
        }
    }

    fun resetarSenha(usuarioId: Int, novaSenha: String, onDone: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = repository.resetarSenha(usuarioId, novaSenha)
            if (result.isSuccess) {
                _actionState.value = UiState.Success(Unit)
                onDone()
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao resetar senha")
            }
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}
