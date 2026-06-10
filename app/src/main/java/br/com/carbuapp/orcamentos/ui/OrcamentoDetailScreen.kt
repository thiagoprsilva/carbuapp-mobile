package br.com.carbuapp.orcamentos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.orcamentos.domain.model.OrcamentoDetalhe
import br.com.carbuapp.orcamentos.domain.model.OrcamentoItem
import br.com.carbuapp.orcamentos.domain.model.STATUS_ORCAMENTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrcamentoDetailScreen(
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: OrcamentoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusSheet by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        if (actionState is UiState.Error) {
            snackbarHost.showSnackbar((actionState as UiState.Error).message)
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe do Orçamento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    if (uiState is UiState.Success) {
                        val id = (uiState as UiState.Success<OrcamentoDetalhe>).data.orcamento.id
                        // PDF
                        IconButton(onClick = { viewModel.sharePdf(context) }) {
                            Icon(Icons.Default.PictureAsPdf, "Gerar PDF")
                        }
                        // WhatsApp
                        IconButton(onClick = { viewModel.openWhatsApp(context) }) {
                            Icon(Icons.Default.Share, "Compartilhar WhatsApp")
                        }
                        // Status
                        IconButton(onClick = { showStatusSheet = true }) {
                            Icon(Icons.Default.Edit, "Alterar status")
                        }
                        // Editar
                        IconButton(onClick = { onEdit(id) }) {
                            Icon(Icons.Default.Create, "Editar")
                        }
                        // Excluir
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Excluir")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = uiState) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UiState.Error   -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load) { Text("Tentar novamente") }
                }
                is UiState.Success -> OrcamentoContent(
                    detalhe = s.data,
                    actionLoading = actionState is UiState.Loading
                )
                else -> Unit
            }
        }
    }

    // ── Excluir ──────────────────────────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir orçamento") },
            text = { Text("Tem certeza que deseja excluir este orçamento?") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; viewModel.delete(onDeleted = onBack) }) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    // ── Bottom Sheet: alterar status ─────────────────────────────────────────
    if (showStatusSheet) {
        ModalBottomSheet(onDismissRequest = { showStatusSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Alterar status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                val currentStatus = (uiState as? UiState.Success<OrcamentoDetalhe>)?.data?.orcamento?.status
                STATUS_ORCAMENTO.forEach { status ->
                    val isSelected = status == currentStatus
                    val (containerColor, contentColor) = orcamentoStatusColors(status)
                    Surface(
                        onClick = { showStatusSheet = false; viewModel.changeStatus(status) },
                        shape = MaterialTheme.shapes.medium,
                        color = if (isSelected) containerColor else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = status,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) contentColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isSelected) Icon(
                                Icons.Default.Check, contentDescription = null,
                                tint = contentColor, modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Cores por status de orçamento ────────────────────────────────────────────

@Composable
fun orcamentoStatusColors(status: String): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> = when (status) {
    "Pendente"  -> MaterialTheme.colorScheme.tertiaryContainer  to MaterialTheme.colorScheme.onTertiaryContainer
    "Aprovado"  -> MaterialTheme.colorScheme.primaryContainer   to MaterialTheme.colorScheme.onPrimaryContainer
    "Rejeitado" -> MaterialTheme.colorScheme.errorContainer     to MaterialTheme.colorScheme.onErrorContainer
    "Executado" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    else        -> MaterialTheme.colorScheme.surfaceVariant     to MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
fun OrcamentoStatusChip(status: String) {
    val (containerColor, contentColor) = orcamentoStatusColors(status)
    Surface(shape = MaterialTheme.shapes.small, color = containerColor) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
private fun OrcamentoContent(detalhe: OrcamentoDetalhe, actionLoading: Boolean) {
    val orc = detalhe.orcamento
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Orçamento #${orc.numero}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            OrcamentoStatusChip(status = orc.status)
        }

        if (actionLoading) LinearProgressIndicator(Modifier.fillMaxWidth())

        // OS / Veículo / Cliente
        InfoRow(label = "OS vinculada", value = "OS #${orc.osNumero}")
        InfoRow(label = "Veículo",      value = "${orc.placa} · ${orc.modelo}")
        InfoRow(label = "Cliente",      value = orc.clienteNome)
        if (!orc.clienteTelefone.isNullOrBlank()) {
            InfoRow(label = "Telefone", value = orc.clienteTelefone)
        }
        InfoRow(label = "Data",         value = orc.createdAt.take(10))

        HorizontalDivider()

        // Itens
        Text("Itens do orçamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

        if (detalhe.itens.isEmpty()) {
            Text("Nenhum item carregado", color = MaterialTheme.colorScheme.outline)
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        Text("Descrição", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("Qtd",   style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.width(16.dp))
                        Text("Unit.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.width(16.dp))
                        Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                    HorizontalDivider()
                    detalhe.itens.forEach { item -> ItemRow(item) }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            "Total: R$ ${"%.2f".format(orc.total)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ItemRow(item: OrcamentoItem) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(item.descricao, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text("${"%.1f".format(item.qtd)}x", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.width(16.dp))
        Text("${"%.2f".format(item.precoUnit)}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.width(16.dp))
        Text(
            "R$ ${"%.2f".format(item.valorLinha)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
