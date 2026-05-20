package br.com.carbuapp.veiculos.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.veiculos.domain.VeiculoCreateRequest
import br.com.carbuapp.veiculos.domain.usecase.GetVeiculoByIdUseCase
import br.com.carbuapp.veiculos.domain.usecase.SaveVeiculoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VeiculoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getVeiculoByIdUseCase: GetVeiculoByIdUseCase,
    private val saveVeiculoUseCase: SaveVeiculoUseCase
) : ViewModel() {

    private val veiculoId: Int? = savedStateHandle.get<Int>("veiculoId")?.takeIf { it != -1 }
    val clienteIdParam: Int? = savedStateHandle.get<Int>("clienteId")?.takeIf { it != -1 }
    val isEditing: Boolean = veiculoId != null

    val placa       = MutableStateFlow("")
    val modelo      = MutableStateFlow("")
    val ano         = MutableStateFlow("")
    val motor       = MutableStateFlow("")
    val alimentacao = MutableStateFlow("")
    val clienteId   = MutableStateFlow(clienteIdParam ?: 0)

    private val _loadState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loadState: StateFlow<UiState<Unit>> = _loadState.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val saveState: StateFlow<UiState<Unit>> = _saveState.asStateFlow()

    val alimentacaoOpcoes = listOf("Carburado", "Injeção", "Turbo", "Stage")

    init {
        if (isEditing) loadExisting()
    }

    private fun loadExisting() {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            val result = getVeiculoByIdUseCase(veiculoId!!)
            if (result.isSuccess) {
                val v = result.getOrThrow()
                placa.value       = v.placa
                modelo.value      = v.modelo
                ano.value         = v.ano ?: ""
                motor.value       = v.motor ?: ""
                alimentacao.value = v.alimentacao ?: ""
                clienteId.value   = v.clienteId
                _loadState.value  = UiState.Success(Unit)
            } else {
                _loadState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar")
            }
        }
    }

    fun save() {
        val placaTrimmed  = placa.value.trim().uppercase()
        val modeloTrimmed = modelo.value.trim()

        if (placaTrimmed.isBlank()) { _saveState.value = UiState.Error("A placa é obrigatória"); return }
        if (modeloTrimmed.isBlank()) { _saveState.value = UiState.Error("O modelo é obrigatório"); return }
        if (clienteId.value == 0) { _saveState.value = UiState.Error("Selecione um cliente"); return }

        val request = VeiculoCreateRequest(
            placa       = placaTrimmed,
            modelo      = modeloTrimmed,
            ano         = ano.value.trim().ifBlank { null },
            motor       = motor.value.trim().ifBlank { null },
            alimentacao = alimentacao.value.ifBlank { null },
            clienteId   = clienteId.value
        )

        viewModelScope.launch {
            _saveState.value = UiState.Loading
            val result = if (isEditing) saveVeiculoUseCase.update(veiculoId!!, request)
                         else saveVeiculoUseCase.create(request)
            _saveState.value = if (result.isSuccess) UiState.Success(Unit)
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao salvar")
        }
    }
}
