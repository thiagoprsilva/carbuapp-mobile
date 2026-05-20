package br.com.carbuapp.ordens.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.ordens.domain.OSCreateRequest
import br.com.carbuapp.ordens.domain.model.OrdemServico
import br.com.carbuapp.ordens.domain.usecase.GetOSByIdUseCase
import br.com.carbuapp.ordens.domain.usecase.SaveOSUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OSFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getOSById: GetOSByIdUseCase,
    private val saveOS: SaveOSUseCase
) : ViewModel() {

    // -1 = nova OS
    private val osId: Int? = savedStateHandle.get<Int>("osId")?.takeIf { it != -1 }
    val veiculoIdPresel: Int? = savedStateHandle.get<Int>("veiculoId")?.takeIf { it != -1 }

    private val _uiState = MutableStateFlow<UiState<OrdemServico>>(UiState.Idle)
    val uiState: StateFlow<UiState<OrdemServico>> = _uiState.asStateFlow()

    // Campos do formulário
    val veiculoId = MutableStateFlow(veiculoIdPresel ?: 0)
    val categoria  = MutableStateFlow("")
    val descricao  = MutableStateFlow("")
    val dataServico = MutableStateFlow("")
    val observacoes = MutableStateFlow("")

    init {
        if (osId != null) loadExisting()
    }

    private fun loadExisting() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = getOSById(osId!!)
            result.onSuccess { detalhe ->
                val os = detalhe.os
                veiculoId.value   = os.veiculoId
                categoria.value   = os.categoria
                descricao.value   = os.descricao
                dataServico.value = os.dataServico
                observacoes.value = os.observacoes ?: ""
                _uiState.value = UiState.Idle
            }.onFailure {
                _uiState.value = UiState.Error(it.message ?: "Erro ao carregar OS")
            }
        }
    }

    fun save() {
        if (categoria.value.isBlank() || descricao.value.isBlank() || dataServico.value.isBlank()) {
            _uiState.value = UiState.Error("Preencha todos os campos obrigatórios")
            return
        }
        if (veiculoId.value == 0) {
            _uiState.value = UiState.Error("Selecione um veículo")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = saveOS(
                id = osId,
                request = OSCreateRequest(
                    veiculoId = veiculoId.value,
                    categoria = categoria.value.trim(),
                    descricao = descricao.value.trim(),
                    dataServico = dataServico.value.trim(),
                    observacoes = observacoes.value.trim().ifBlank { null }
                )
            )
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Erro ao salvar OS") }
            )
        }
    }
}
