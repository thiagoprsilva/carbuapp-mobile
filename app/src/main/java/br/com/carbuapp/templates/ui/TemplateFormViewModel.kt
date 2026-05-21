package br.com.carbuapp.templates.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.templates.domain.TemplateItemInput
import br.com.carbuapp.templates.domain.TemplateSaveRequest
import br.com.carbuapp.templates.domain.usecase.GetTemplatesUseCase
import br.com.carbuapp.templates.domain.usecase.SaveTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TemplateItemFormState(
    val descricao: String = "",
    val qtd: String = "1",
    val precoUnit: String = "0.00"
)

@HiltViewModel
class TemplateFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTemplates: GetTemplatesUseCase,
    private val saveTemplate: SaveTemplateUseCase
) : ViewModel() {

    val templateId: Int? = savedStateHandle.get<Int>("templateId")?.takeIf { it != -1 }

    private val _uiState = MutableStateFlow<UiState<Unit>>(
        if (templateId != null) UiState.Loading else UiState.Success(Unit)
    )
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    private val _nome  = MutableStateFlow("")
    val nome: StateFlow<String> = _nome.asStateFlow()

    private val _itens = MutableStateFlow<List<TemplateItemFormState>>(listOf(TemplateItemFormState()))
    val itens: StateFlow<List<TemplateItemFormState>> = _itens.asStateFlow()

    init {
        if (templateId != null) loadForEdit()
    }

    private fun loadForEdit() {
        viewModelScope.launch {
            try {
                val templates = getTemplates()
                val template  = templates.find { it.id == templateId }
                if (template != null) {
                    _nome.value  = template.nome
                    _itens.value = template.itens.map {
                        TemplateItemFormState(
                            descricao = it.descricao,
                            qtd       = it.qtd.toString(),
                            precoUnit = "%.2f".format(it.precoUnit)
                        )
                    }.ifEmpty { listOf(TemplateItemFormState()) }
                }
                _uiState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun onNomeChange(value: String) { _nome.value = value }

    fun addItem() { _itens.update { it + TemplateItemFormState() } }

    fun removeItem(index: Int) {
        _itens.update { list -> list.filterIndexed { i, _ -> i != index } }
    }

    fun updateDescricao(index: Int, value: String) {
        _itens.update { list -> list.mapIndexed { i, item -> if (i == index) item.copy(descricao = value) else item } }
    }

    fun updateQtd(index: Int, value: String) {
        _itens.update { list -> list.mapIndexed { i, item -> if (i == index) item.copy(qtd = value.filter { it.isDigit() }) else item } }
    }

    fun updatePreco(index: Int, value: String) {
        _itens.update { list -> list.mapIndexed { i, item -> if (i == index) item.copy(precoUnit = value) else item } }
    }

    fun save(onSaved: () -> Unit) {
        val nome = _nome.value.trim()
        if (nome.isBlank()) {
            _actionState.value = UiState.Error("O nome do template é obrigatório.")
            return
        }
        val itensValidos = _itens.value.filter { it.descricao.isNotBlank() }
        if (itensValidos.isEmpty()) {
            _actionState.value = UiState.Error("Adicione pelo menos 1 item com descrição.")
            return
        }

        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                saveTemplate(
                    TemplateSaveRequest(
                        id    = templateId,
                        nome  = nome,
                        itens = itensValidos.map {
                            TemplateItemInput(
                                descricao = it.descricao,
                                qtd       = it.qtd.toIntOrNull() ?: 1,
                                precoUnit = it.precoUnit.toDoubleOrNull() ?: 0.0
                            )
                        }
                    )
                )
                _actionState.value = UiState.Success(Unit)
                onSaved()
            } catch (e: Exception) {
                _actionState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}
