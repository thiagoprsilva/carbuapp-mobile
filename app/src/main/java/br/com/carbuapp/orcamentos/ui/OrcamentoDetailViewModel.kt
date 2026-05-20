package br.com.carbuapp.orcamentos.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.orcamentos.domain.model.OrcamentoDetalhe
import br.com.carbuapp.orcamentos.domain.usecase.DeleteOrcamentoUseCase
import br.com.carbuapp.orcamentos.domain.usecase.GetOrcamentoByIdUseCase
import br.com.carbuapp.orcamentos.domain.usecase.UpdateOrcamentoStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrcamentoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getById: GetOrcamentoByIdUseCase,
    private val updateStatus: UpdateOrcamentoStatusUseCase,
    private val delete: DeleteOrcamentoUseCase
) : ViewModel() {

    private val orcamentoId: Int = checkNotNull(savedStateHandle["orcamentoId"])

    private val _uiState = MutableStateFlow<UiState<OrcamentoDetalhe>>(UiState.Loading)
    val uiState: StateFlow<UiState<OrcamentoDetalhe>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = getById(orcamentoId).fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Erro ao carregar orçamento") }
            )
        }
    }

    fun changeStatus(newStatus: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = updateStatus(orcamentoId, newStatus)
            if (result.isSuccess) {
                load()
                _actionState.value = UiState.Success(Unit)
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao atualizar status")
            }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            delete(orcamentoId).fold(
                onSuccess = { onDeleted() },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "Erro ao excluir") }
            )
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}
