package br.com.carbuapp.veiculos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.veiculos.domain.model.TimelineEvento
import br.com.carbuapp.veiculos.domain.model.Veiculo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculoDetailScreen(
    veiculoId: Int,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOSClick: (Int) -> Unit = {},
    onOrcamentoClick: (Int) -> Unit = {},
    viewModel: VeiculoDetailViewModel = hiltViewModel()
) {
    val veiculoState by viewModel.veiculoState.collectAsStateWithLifecycle()
    val timelineState by viewModel.timelineState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is UiState.Success -> onBack()
            is UiState.Error   -> snackbarHost.showSnackbar((deleteState as UiState.Error).message)
            else               -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veículo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        when (val state = veiculoState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = viewModel::load) { Text("Tentar novamente") }
                }
            }
            is UiState.Success -> {
                val veiculo = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { VeiculoInfoSection(veiculo) }
                    item {
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        Text("Timeline", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                    }
                    when (val tl = timelineState) {
                        is UiState.Loading -> item {
                            Box(Modifier.fillMaxWidth(), Alignment.Center) { CircularProgressIndicator() }
                        }
                        is UiState.Error -> item {
                            Text(tl.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        is UiState.Success -> {
                            if (tl.data.isEmpty()) {
                                item {
                                    Text(
                                        "Nenhum registro ou orçamento ainda.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            } else {
                                items(tl.data, key = { "${it.javaClass.simpleName}_${it.id}" }) { evento ->
                                    TimelineEventoCard(
                                        evento = evento,
                                        onOSClick = onOSClick,
                                        onOrcamentoClick = onOrcamentoClick
                                    )
                                }
                            }
                        }
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir veículo") },
            text = { Text("Só é possível excluir veículos sem OS ou orçamentos vinculados.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; viewModel.delete() }) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun VeiculoInfoSection(veiculo: Veiculo) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "${veiculo.placa}  ·  ${veiculo.modelo}",
            style = MaterialTheme.typography.headlineMedium
        )
        if (!veiculo.clienteNome.isNullOrBlank()) {
            Text(
                text = "Cliente: ${veiculo.clienteNome}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        val detalhes = listOfNotNull(
            veiculo.ano?.let { "Ano: $it" },
            veiculo.motor?.let { "Motor: $it" },
            veiculo.alimentacao?.let { "Sistema: $it" }
        )
        detalhes.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
    }
}

@Composable
private fun TimelineEventoCard(
    evento: TimelineEvento,
    onOSClick: (Int) -> Unit,
    onOrcamentoClick: (Int) -> Unit
) {
    val isRegistro = evento is TimelineEvento.Registro
    Card(
        onClick = {
            if (isRegistro) onOSClick(evento.id)
            else onOrcamentoClick(evento.id)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRegistro)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isRegistro) Icons.Default.Build else Icons.Default.Receipt,
                contentDescription = null,
                tint = if (isRegistro) MaterialTheme.colorScheme.onSecondaryContainer
                       else MaterialTheme.colorScheme.onTertiaryContainer
            )
            Column(modifier = Modifier.weight(1f)) {
                when (evento) {
                    is TimelineEvento.Registro -> {
                        Text(evento.categoria, style = MaterialTheme.typography.labelMedium)
                        Text(evento.descricao, style = MaterialTheme.typography.bodyMedium)
                    }
                    is TimelineEvento.Orcamento -> {
                        Text("Orçamento #${evento.numero}", style = MaterialTheme.typography.labelMedium)
                        Text("R$ ${"%.2f".format(evento.total)}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Text(
                    text = evento.data.take(10),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
