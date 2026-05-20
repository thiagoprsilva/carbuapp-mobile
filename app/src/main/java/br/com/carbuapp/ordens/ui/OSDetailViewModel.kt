package br.com.carbuapp.ordens.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.ordens.domain.model.OrdemServicoDetalhe
import br.com.carbuapp.ordens.domain.usecase.DeleteOSUseCase
import br.com.carbuapp.ordens.domain.usecase.GetOSByIdUseCase
import br.com.carbuapp.ordens.domain.usecase.UpdateOSStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OSDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getOSById: GetOSByIdUseCase,
    private val updateStatus: UpdateOSStatusUseCase,
    private val deleteOS: DeleteOSUseCase
) : ViewModel() {

    private val osId: Int = checkNotNull(savedStateHandle["osId"])

    private val _uiState = MutableStateFlow<UiState<OrdemServicoDetalhe>>(UiState.Loading)
    val uiState: StateFlow<UiState<OrdemServicoDetalhe>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = getOSById(osId)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Erro ao carregar OS") }
            )
        }
    }

    fun changeStatus(newStatus: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = updateStatus(osId, newStatus)
            if (result.isSuccess) {
                load() // recarrega os dados completos
                _actionState.value = UiState.Success(Unit)
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao atualizar status")
            }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = deleteOS(osId)
            if (result.isSuccess) {
                onDeleted()
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao excluir OS")
            }
        }
    }

    fun resetActionState() {
        _actionState.value = UiState.Idle
    }
}
