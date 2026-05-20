package br.com.carbuapp.ordens.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.ordens.domain.model.OrdemServico
import br.com.carbuapp.ordens.domain.usecase.GetOSUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OSListViewModel @Inject constructor(
    private val getOS: GetOSUseCase
) : ViewModel() {

    // null = todos os status
    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val ordens: StateFlow<List<OrdemServico>> = _statusFilter
        .flatMapLatest { status ->
            if (status == null) getOS.observeAll()
            else getOS.observeByStatus(status)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _refreshState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val refreshState: StateFlow<UiState<Unit>> = _refreshState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshState.value = UiState.Loading
            val result = getOS.refresh()
            _refreshState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Erro ao carregar ordens") }
            )
        }
    }

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
    }
}
