package br.com.carbuapp.orcamentos.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.orcamentos.domain.OrcamentoRepository
import br.com.carbuapp.orcamentos.domain.model.OrcamentoDetalhe
import br.com.carbuapp.orcamentos.domain.usecase.DeleteOrcamentoUseCase
import br.com.carbuapp.orcamentos.domain.usecase.GetOrcamentoByIdUseCase
import br.com.carbuapp.orcamentos.domain.usecase.UpdateOrcamentoStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class OrcamentoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getById: GetOrcamentoByIdUseCase,
    private val updateStatus: UpdateOrcamentoStatusUseCase,
    private val delete: DeleteOrcamentoUseCase,
    private val repository: OrcamentoRepository
) : ViewModel() {

    private val orcamentoId: Int = checkNotNull(savedStateHandle["orcamentoId"])

    private val _uiState = MutableStateFlow<UiState<OrcamentoDetalhe>>(UiState.Loading)
    val uiState: StateFlow<UiState<OrcamentoDetalhe>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = getById(orcamentoId).fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Erro ao carregar orçamento") }
            )
        }
    }

    fun changeStatus(newStatus: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = updateStatus(orcamentoId, newStatus)
            if (result.isSuccess) {
                load()
                _actionState.value = UiState.Success(Unit)
            } else {
                _actionState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao atualizar status")
            }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            delete(orcamentoId).fold(
                onSuccess = { onDeleted() },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "Erro ao excluir") }
            )
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }

    // ── PDF ───────────────────────────────────────────────────────────────────

    fun sharePdf(context: Context) {
        val orc = (uiState.value as? UiState.Success<OrcamentoDetalhe>)?.data?.orcamento ?: return
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            repository.getPdf(orc.id).fold(
                onSuccess = { bytes ->
                    withContext(Dispatchers.IO) {
                        val dir = File(context.cacheDir, "pdfs").apply { mkdirs() }
                        val file = File(dir, "orcamento_${orc.numero}.pdf")
                        file.writeBytes(bytes)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, "Orçamento #${orc.numero} — ${orc.clienteNome}")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartilhar orçamento"))
                    }
                    _actionState.value = UiState.Idle
                },
                onFailure = { e ->
                    _actionState.value = UiState.Error(e.message ?: "Erro ao gerar PDF")
                }
            )
        }
    }

    // ── WhatsApp ──────────────────────────────────────────────────────────────

    fun openWhatsApp(context: Context) {
        val detalhe = (uiState.value as? UiState.Success<OrcamentoDetalhe>)?.data ?: return
        val orc = detalhe.orcamento

        val itensText = detalhe.itens.joinToString("\n") { item ->
            "  • ${item.descricao}: ${"%.0f".format(item.qtd)}x R$ ${"%.2f".format(item.precoUnit)}"
        }
        val message = buildString {
            appendLine("🔧 *Orçamento #${orc.numero} — CarbuApp*")
            appendLine("Veículo: ${orc.placa} · ${orc.modelo}")
            appendLine("Cliente: ${orc.clienteNome}")
            appendLine("Status: ${orc.status}")
            appendLine()
            appendLine("*Itens:*")
            appendLine(itensText)
            appendLine()
            appendLine("*Total: R$ ${"%.2f".format(orc.total)}*")
        }

        val phone = orc.clienteTelefone
            ?.replace(Regex("[^0-9]"), "")
            ?.let { if (it.startsWith("55")) it else "55$it" }

        val uri = if (!phone.isNullOrBlank()) {
            Uri.parse("https://wa.me/$phone?text=${Uri.encode(message)}")
        } else {
            Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
        }

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            // WhatsApp não instalado — abre o link no browser
            context.startActivity(
                Intent(Intent.ACTION_VIEW, uri).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            )
        }
    }
}
