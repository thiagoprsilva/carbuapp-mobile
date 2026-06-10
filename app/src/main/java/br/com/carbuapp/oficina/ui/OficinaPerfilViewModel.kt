package br.com.carbuapp.oficina.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.auth.domain.AuthRepository
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.oficina.domain.Oficina
import br.com.carbuapp.oficina.domain.OficinaRepository
import br.com.carbuapp.oficina.domain.OficinaUpdateInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OficinaPerfilViewModel @Inject constructor(
    private val repository: OficinaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Oficina>>(UiState.Loading)
    val uiState: StateFlow<UiState<Oficina>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    // Form fields
    val nome        = MutableStateFlow("")
    val responsavel = MutableStateFlow("")
    val telefone    = MutableStateFlow("")
    val endereco    = MutableStateFlow("")

    private var oficinaId: Int = -1

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val userResult = authRepository.me()
            if (userResult.isFailure) {
                _uiState.value = UiState.Error("Não foi possível identificar o usuário.")
                return@launch
            }
            val user = userResult.getOrThrow()
            val id   = user.oficinaId ?: run {
                _uiState.value = UiState.Error("Nenhuma oficina associada ao seu usuário.")
                return@launch
            }
            oficinaId = id
            val result = repository.getById(id)
            if (result.isSuccess) {
                val oficina = result.getOrThrow()
                nome.value        = oficina.nome
                responsavel.value = oficina.responsavel
                telefone.value    = oficina.telefone
                endereco.value    = oficina.endereco
                _uiState.value    = UiState.Success(oficina)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar oficina")
            }
        }
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = repository.update(
                id    = oficinaId,
                input = OficinaUpdateInput(
                    nome        = nome.value.trim(),
                    responsavel = responsavel.value.trim(),
                    telefone    = telefone.value.trim(),
                    endereco    = endereco.value.trim()
                )
            )
            if (result.isSuccess) {
                _actionState.value = UiState.Success(Unit)
                onSaved()
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao salvar")
            }
        }
    }

    fun uploadLogo(uri: Uri) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = repository.uploadLogo(oficinaId, uri)
            if (result.isSuccess) {
                _uiState.value = UiState.Success(result.getOrThrow())
                _actionState.value = UiState.Success(Unit)
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao enviar logo")
            }
        }
    }

    fun deleteLogo() {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = repository.deleteLogo(oficinaId)
            if (result.isSuccess) {
                val oficina = (_uiState.value as? UiState.Success)?.data?.copy(logoUrl = null)
                if (oficina != null) _uiState.value = UiState.Success(oficina)
                _actionState.value = UiState.Success(Unit)
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao remover logo")
            }
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}
