package br.com.carbuapp.laudos.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.laudos.domain.AvariaInput
import br.com.carbuapp.laudos.domain.LaudoSaveRequest
import br.com.carbuapp.laudos.domain.model.Laudo
import br.com.carbuapp.laudos.domain.usecase.DeleteLaudoUseCase
import br.com.carbuapp.laudos.domain.usecase.GetLaudoUseCase
import br.com.carbuapp.laudos.domain.usecase.SaveLaudoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AvariaFormState(
    val id: Int = 0,            // 0 = nova; > 0 = existente
    val zona: String = "",
    val severidade: String? = null,
    val observacao: String = ""
)

@HiltViewModel
class LaudoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getLaudo: GetLaudoUseCase,
    private val saveLaudo: SaveLaudoUseCase,
    private val deleteLaudo: DeleteLaudoUseCase
) : ViewModel() {

    val osId: Int = checkNotNull(savedStateHandle["osId"])

    // ── Estado de carregamento / ação ─────────────────────────────────────────
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    // ── Campos do formulário ──────────────────────────────────────────────────
    private val _km          = MutableStateFlow("")
    val km: StateFlow<String> = _km.asStateFlow()

    private val _nivelCombust          = MutableStateFlow<String?>(null)
    val nivelCombust: StateFlow<String?> = _nivelCombust.asStateFlow()

    private val _observacoes          = MutableStateFlow("")
    val observacoes: StateFlow<String> = _observacoes.asStateFlow()

    private val _avarias               = MutableStateFlow<List<AvariaFormState>>(emptyList())
    val avarias: StateFlow<List<AvariaFormState>> = _avarias.asStateFlow()

    /** true enquanto o laudo existir na API / cache */
    private val _laudoExistente          = MutableStateFlow(false)
    val laudoExistente: StateFlow<Boolean> = _laudoExistente.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val laudo = getLaudo(osId)
                if (laudo != null) {
                    populate(laudo)
                    _laudoExistente.value = true
                } else {
                    _laudoExistente.value = false
                }
                _uiState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    private fun populate(laudo: Laudo) {
        _km.value          = laudo.km?.toString() ?: ""
        _nivelCombust.value = laudo.nivelCombust
        _observacoes.value  = laudo.observacoes ?: ""
        _avarias.value      = laudo.avarias.map {
            AvariaFormState(id = it.id, zona = it.zona, severidade = it.severidade, observacao = it.observacao ?: "")
        }
    }

    // ── Form helpers ──────────────────────────────────────────────────────────

    fun onKmChange(value: String)           { _km.value = value.filter { it.isDigit() } }
    fun onNivelChange(value: String?)        { _nivelCombust.value = value }
    fun onObservacoesChange(value: String)   { _observacoes.value = value }

    fun addAvaria() {
        _avarias.update { it + AvariaFormState() }
    }

    fun removeAvaria(index: Int) {
        _avarias.update { list -> list.filterIndexed { i, _ -> i != index } }
    }

    fun updateAvariaZona(index: Int, zona: String) {
        _avarias.update { list ->
            list.mapIndexed { i, a -> if (i == index) a.copy(zona = zona) else a }
        }
    }

    fun updateAvariaSeveridade(index: Int, severidade: String?) {
        _avarias.update { list ->
            list.mapIndexed { i, a -> if (i == index) a.copy(severidade = severidade) else a }
        }
    }

    fun updateAvariaObservacao(index: Int, observacao: String) {
        _avarias.update { list ->
            list.mapIndexed { i, a -> if (i == index) a.copy(observacao = observacao) else a }
        }
    }

    // ── Persistência ──────────────────────────────────────────────────────────

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                val request = LaudoSaveRequest(
                    osId         = osId,
                    km           = _km.value.toIntOrNull(),
                    nivelCombust = _nivelCombust.value,
                    observacoes  = _observacoes.value.ifBlank { null },
                    avarias      = _avarias.value
                        .filter { it.zona.isNotBlank() }
                        .map { AvariaInput(zona = it.zona, severidade = it.severidade, observacao = it.observacao.ifBlank { null }) }
                )
                saveLaudo(request)
                _laudoExistente.value = true
                _actionState.value    = UiState.Success(Unit)
                onSaved()
            } catch (e: Exception) {
                _actionState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                deleteLaudo(osId)
                _laudoExistente.value = false
                _km.value           = ""
                _nivelCombust.value = null
                _observacoes.value  = ""
                _avarias.value      = emptyList()
                _actionState.value  = UiState.Success(Unit)
                onDeleted()
            } catch (e: Exception) {
                _actionState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}
