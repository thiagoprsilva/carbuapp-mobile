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
    val snackbarHost = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

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
                        IconButton(onClick = { showStatusDialog = true }) {
                            Icon(Icons.Default.Edit, "Alterar status")
                        }
                        IconButton(onClick = { onEdit(id) }) {
                            Icon(Icons.Default.Create, "Editar")
                        }
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

    // ── Alterar status ────────────────────────────────────────────────────────
    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Alterar status") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    STATUS_ORCAMENTO.forEach { status ->
                        TextButton(
                            onClick = { showStatusDialog = false; viewModel.changeStatus(status) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(status) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showStatusDialog = false }) { Text("Cancelar") } }
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
        InfoRow(label = "Data",         value = orc.createdAt.take(10))

        HorizontalDivider()

        // Itens
        Text("Itens do orçamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

        if (detalhe.itens.isEmpty()) {
            Text("Nenhum item carregado", color = MaterialTheme.colorScheme.outline)
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Cabeçalho
                    Row(Modifier.fillMaxWidth()) {
                        Text("Descrição", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("Qtd", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
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
