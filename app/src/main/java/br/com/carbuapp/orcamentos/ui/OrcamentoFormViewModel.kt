package br.com.carbuapp.orcamentos.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.orcamentos.domain.OrcamentoItemInput
import br.com.carbuapp.orcamentos.domain.model.Orcamento
import br.com.carbuapp.orcamentos.domain.usecase.GetOrcamentoByIdUseCase
import br.com.carbuapp.orcamentos.domain.usecase.SaveOrcamentoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Item editável no formulário (mutable para edição linha a linha)
data class ItemFormState(
    val descricao: String = "",
    val qtd: String = "1",
    val precoUnit: String = "0.00"
)

@HiltViewModel
class OrcamentoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getById: GetOrcamentoByIdUseCase,
    private val save: SaveOrcamentoUseCase
) : ViewModel() {

    private val orcamentoId: Int? = savedStateHandle.get<Int>("orcamentoId")?.takeIf { it != -1 }
    val osIdPresel: Int? = savedStateHandle.get<Int>("osId")?.takeIf { it != -1 }

    private val _uiState = MutableStateFlow<UiState<Orcamento>>(UiState.Idle)
    val uiState: StateFlow<UiState<Orcamento>> = _uiState.asStateFlow()

    val osId = MutableStateFlow(osIdPresel ?: 0)

    private val _itens = MutableStateFlow(listOf(ItemFormState()))
    val itens: StateFlow<List<ItemFormState>> = _itens.asStateFlow()

    init { if (orcamentoId != null) loadExisting() }

    private fun loadExisting() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getById(orcamentoId!!).onSuccess { detalhe ->
                osId.value = detalhe.orcamento.osId
                _itens.value = detalhe.itens.map { item ->
                    ItemFormState(
                        descricao = item.descricao,
                        qtd       = item.qtd.toString(),
                        precoUnit = item.precoUnit.toString()
                    )
                }.ifEmpty { listOf(ItemFormState()) }
                _uiState.value = UiState.Idle
            }.onFailure {
                _uiState.value = UiState.Error(it.message ?: "Erro ao carregar orçamento")
            }
        }
    }

    fun updateItem(index: Int, item: ItemFormState) {
        _itens.value = _itens.value.toMutableList().also { it[index] = item }
    }

    fun addItem() {
        _itens.value = _itens.value + ItemFormState()
    }

    fun removeItem(index: Int) {
        if (_itens.value.size > 1)
            _itens.value = _itens.value.toMutableList().also { it.removeAt(index) }
    }

    fun save() {
        if (osId.value == 0) {
            _uiState.value = UiState.Error("Selecione uma OS para o orçamento")
            return
        }
        val inputs = _itens.value.mapNotNull { item ->
            val descricao = item.descricao.trim()
            val qtd       = item.qtd.toDoubleOrNull() ?: 0.0
            val preco     = item.precoUnit.toDoubleOrNull() ?: 0.0
            if (descricao.isBlank() || qtd <= 0) null
            else OrcamentoItemInput(descricao, qtd, preco)
        }
        if (inputs.isEmpty()) {
            _uiState.value = UiState.Error("Adicione pelo menos um item válido")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = save(orcamentoId, osId.value, inputs).fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Erro ao salvar orçamento") }
            )
        }
    }
}
