package br.com.carbuapp.usuarios.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.usuarios.domain.UsuarioCreateInput
import br.com.carbuapp.usuarios.domain.UsuarioRepository
import br.com.carbuapp.usuarios.domain.UsuarioUpdateInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: UsuarioRepository
) : ViewModel() {

    val usuarioId: Int? = savedStateHandle.get<Int>("usuarioId")?.takeIf { it != -1 }
    val isEdit: Boolean get() = usuarioId != null

    private val _uiState = MutableStateFlow<UiState<Unit>>(
        if (usuarioId != null) UiState.Loading else UiState.Success(Unit)
    )
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    // Form fields
    val nome  = MutableStateFlow("")
    val email = MutableStateFlow("")
    val senha = MutableStateFlow("")
    val role  = MutableStateFlow("MECANICO")
    val ativo = MutableStateFlow(true)

    init {
        if (usuarioId != null) loadForEdit()
    }

    private fun loadForEdit() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.listar()
            if (result.isSuccess) {
                val usuario = result.getOrThrow().find { it.id == usuarioId }
                if (usuario != null) {
                    nome.value  = usuario.nome
                    email.value = usuario.email
                    role.value  = usuario.role
                    ativo.value = usuario.ativo
                }
                _uiState.value = UiState.Success(Unit)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar usuário")
            }
        }
    }

    fun save(onSaved: () -> Unit) {
        val nomeVal  = nome.value.trim()
        val emailVal = email.value.trim()
        val senhaVal = senha.value.trim()
        val roleVal  = role.value

        if (nomeVal.isBlank()) {
            _actionState.value = UiState.Error("O nome é obrigatório.")
            return
        }
        if (emailVal.isBlank()) {
            _actionState.value = UiState.Error("O e-mail é obrigatório.")
            return
        }
        if (!isEdit && senhaVal.length < 6) {
            _actionState.value = UiState.Error("A senha deve ter ao menos 6 caracteres.")
            return
        }

        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = if (isEdit) {
                repository.atualizar(
                    id    = usuarioId!!,
                    input = UsuarioUpdateInput(
                        nome  = nomeVal,
                        email = emailVal,
                        role  = roleVal,
                        ativo = ativo.value
                    )
                )
            } else {
                repository.criar(
                    UsuarioCreateInput(
                        nome  = nomeVal,
                        email = emailVal,
                        senha = senhaVal,
                        role  = roleVal
                    )
                )
            }

            if (result.isSuccess) {
                _actionState.value = UiState.Success(Unit)
                onSaved()
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao salvar")
            }
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}
