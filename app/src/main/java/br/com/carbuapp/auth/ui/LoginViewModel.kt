package br.com.carbuapp.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.auth.domain.AuthRepository
import br.com.carbuapp.auth.domain.model.User
import br.com.carbuapp.core.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Carrega UiState<User> para que o nav saiba o role após o login
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val uiState: StateFlow<UiState<User>> = _uiState

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _uiState.value = UiState.Error("Preencha email e senha.")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = authRepository.login(email.trim(), senha)
            _uiState.value = if (result.isSuccess) UiState.Success(result.getOrThrow())
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao fazer login.")
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
