package br.com.carbuapp.fotos.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.core.util.parseHttpError
import br.com.carbuapp.fotos.domain.model.Foto
import br.com.carbuapp.fotos.domain.usecase.DeleteFotoUseCase
import br.com.carbuapp.fotos.domain.usecase.GetFotosUseCase
import br.com.carbuapp.fotos.domain.usecase.UploadFotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FotoGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFotos: GetFotosUseCase,
    private val uploadFoto: UploadFotoUseCase,
    private val deleteFoto: DeleteFotoUseCase
) : ViewModel() {

    val osId: Int = checkNotNull(savedStateHandle["osId"])

    private val _uiState = MutableStateFlow<UiState<List<Foto>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Foto>>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val fotos = getFotos(osId)
                _uiState.value = if (fotos.isEmpty()) UiState.Empty else UiState.Success(fotos)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun upload(uri: Uri) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                uploadFoto(osId, uri)
                _actionState.value = UiState.Success(Unit)
                load() // Recarrega a galeria
            } catch (e: Exception) {
                _actionState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun delete(fotoId: Int) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                deleteFoto(osId, fotoId)
                _actionState.value = UiState.Success(Unit)
                // Remove da lista localmente sem recarregar tudo
                val current = (_uiState.value as? UiState.Success)?.data ?: return@launch
                val updated = current.filter { it.id != fotoId }
                _uiState.value = if (updated.isEmpty()) UiState.Empty else UiState.Success(updated)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(parseHttpError(e))
            }
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}
