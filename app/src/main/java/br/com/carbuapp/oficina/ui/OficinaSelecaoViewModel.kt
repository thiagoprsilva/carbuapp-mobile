package br.com.carbuapp.oficina.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.data.TokenDataStore
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.oficina.domain.Oficina
import br.com.carbuapp.oficina.domain.OficinaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OficinaSelecaoViewModel @Inject constructor(
    private val repository: OficinaRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Oficina>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Oficina>>> = _uiState.asStateFlow()

    private val _selectState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val selectState: StateFlow<UiState<Unit>> = _selectState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.listarTodas()
            _uiState.value = if (result.isSuccess) UiState.Success(result.getOrThrow())
            else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar oficinas")
        }
    }

    fun selecionarOficina(oficina: Oficina) {
        viewModelScope.launch {
            tokenDataStore.saveSelectedOficinaId(oficina.id)
            _selectState.value = UiState.Success(Unit)
        }
    }
}
