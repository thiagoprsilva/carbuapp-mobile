package br.com.carbuapp.templates.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.templates.domain.model.Template
import br.com.carbuapp.templates.domain.usecase.DeleteTemplateUseCase
import br.com.carbuapp.templates.domain.usecase.GetTemplatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateListViewModel @Inject constructor(
    private val getTemplates: GetTemplatesUseCase,
    private val deleteTemplate: DeleteTemplateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Template>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Template>>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val list = getTemplates()
                _uiState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                deleteTemplate(id)
                _actionState.value = UiState.Success(Unit)
                val current = (_uiState.value as? UiState.Success)?.data ?: return@launch
                val updated = current.filter { it.id != id }
                _uiState.value = if (updated.isEmpty()) UiState.Empty else UiState.Success(updated)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}
