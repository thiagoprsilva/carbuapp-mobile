package br.com.carbuapp.veiculos.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.veiculos.domain.model.TimelineEvento
import br.com.carbuapp.veiculos.domain.model.Veiculo
import br.com.carbuapp.veiculos.domain.usecase.DeleteVeiculoUseCase
import br.com.carbuapp.veiculos.domain.usecase.GetVeiculoByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VeiculoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getVeiculoByIdUseCase: GetVeiculoByIdUseCase,
    private val deleteVeiculoUseCase: DeleteVeiculoUseCase
) : ViewModel() {

    private val veiculoId: Int = checkNotNull(savedStateHandle["veiculoId"])

    private val _veiculoState = MutableStateFlow<UiState<Veiculo>>(UiState.Loading)
    val veiculoState: StateFlow<UiState<Veiculo>> = _veiculoState.asStateFlow()

    private val _timelineState = MutableStateFlow<UiState<List<TimelineEvento>>>(UiState.Loading)
    val timelineState: StateFlow<UiState<List<TimelineEvento>>> = _timelineState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _veiculoState.value = UiState.Loading
            val result = getVeiculoByIdUseCase(veiculoId)
            _veiculoState.value = if (result.isSuccess) UiState.Success(result.getOrThrow())
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar veículo")
        }
        loadTimeline()
    }

    private fun loadTimeline() {
        viewModelScope.launch {
            _timelineState.value = UiState.Loading
            val result = getVeiculoByIdUseCase.timeline(veiculoId)
            _timelineState.value = if (result.isSuccess) UiState.Success(result.getOrThrow())
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar timeline")
        }
    }

    fun delete() {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            val result = deleteVeiculoUseCase(veiculoId)
            _deleteState.value = if (result.isSuccess) UiState.Success(Unit)
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao excluir veículo")
        }
    }
}
