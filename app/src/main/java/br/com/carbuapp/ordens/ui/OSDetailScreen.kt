package br.com.carbuapp.ordens.ui

import androidx.compose.foundation.clickable
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
import br.com.carbuapp.dashboard.ui.StatusChip
import br.com.carbuapp.ordens.domain.model.OrdemServicoDetalhe
import br.com.carbuapp.ordens.domain.model.STATUS_OS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OSDetailScreen(
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onLaudo: (Int) -> Unit = {},
    onFotos: (Int) -> Unit = {},
    onNewOrcamento: (Int) -> Unit = {},
    onOrcamentos: () -> Unit = {},
    viewModel: OSDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusSheet by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        when (val s = actionState) {
            is UiState.Error -> {
                snackbarHost.showSnackbar(s.message)
                viewModel.resetActionState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe da OS") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (uiState is UiState.Success) {
                        val detalhe = (uiState as UiState.Success<OrdemServicoDetalhe>).data
                        IconButton(onClick = { showStatusSheet = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Alterar status")
                        }
                        IconButton(onClick = { onEdit(detalhe.os.id) }) {
                            Icon(Icons.Default.Create, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is UiState.Success) {
                val osId = (uiState as UiState.Success<OrdemServicoDetalhe>).data.os.id
                ExtendedFloatingActionButton(
                    text = { Text("Novo orçamento") },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    onClick = { onNewOrcamento(osId) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Error   -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load) { Text("Tentar novamente") }
                }
                is UiState.Success -> OSDetalheContent(
                    detalhe       = state.data,
                    actionLoading = actionState is UiState.Loading,
                    onLaudo       = { onLaudo(state.data.os.id) },
                    onFotos       = { onFotos(state.data.os.id) },
                    onOrcamentos  = onOrcamentos
                )
                else -> Unit
            }
        }
    }

    // ── Diálogo: confirmar exclusão ──────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir OS") },
            text = { Text("Tem certeza que deseja excluir esta ordem de serviço? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteDialog = false; viewModel.delete(onDeleted = onBack) }
                ) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
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
                    text = "Alterar status da OS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                val currentStatus = (uiState as? UiState.Success<OrdemServicoDetalhe>)?.data?.os?.status
                STATUS_OS.forEach { status ->
                    val isSelected = status == currentStatus
                    val (containerColor, contentColor) = osStatusColors(status)
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

@Composable
fun osStatusColors(status: String): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> = when (status) {
    "Aberta"           -> MaterialTheme.colorScheme.primaryContainer   to MaterialTheme.colorScheme.onPrimaryContainer
    "Em andamento"     -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    "Aguardando peças" -> MaterialTheme.colorScheme.tertiaryContainer  to MaterialTheme.colorScheme.onTertiaryContainer
    "Concluída"        -> MaterialTheme.colorScheme.surfaceVariant     to MaterialTheme.colorScheme.onSurfaceVariant
    "Cancelada"        -> MaterialTheme.colorScheme.errorContainer     to MaterialTheme.colorScheme.onErrorContainer
    else               -> MaterialTheme.colorScheme.surfaceVariant     to MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun OSDetalheContent(
    detalhe: OrdemServicoDetalhe,
    actionLoading: Boolean,
    onLaudo:      () -> Unit = {},
    onFotos:      () -> Unit = {},
    onOrcamentos: () -> Unit = {}
) {
    val os = detalhe.os
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 88.dp), // espaço para o FAB
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header com número e status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OS #${os.numero}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            StatusChip(status = os.status)
        }

        if (actionLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

        // Veículo e Cliente
        OSInfoCard(icon = Icons.Default.DirectionsCar, title = "Veículo",  content = "${os.placa} · ${os.modelo}")
        OSInfoCard(icon = Icons.Default.Person,         title = "Cliente", content = os.clienteNome)

        // Dados da OS
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OSDetailRow(label = "Categoria",      value = os.categoria)
                HorizontalDivider()
                OSDetailRow(label = "Data do serviço", value = os.dataServico)
                HorizontalDivider()
                OSDetailRow(label = "Descrição",       value = os.descricao)
                if (!os.observacoes.isNullOrBlank()) {
                    HorizontalDivider()
                    OSDetailRow(label = "Observações", value = os.observacoes)
                }
            }
        }

        // Sub-recursos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SubRecursoChip(
                modifier = Modifier.weight(1f).clickable(onClick = onLaudo),
                icon = Icons.Default.Description,
                label = "Laudo",
                value = if (detalhe.temLaudo) "Preenchido" else "Sem laudo",
                filled = detalhe.temLaudo
            )
            SubRecursoChip(
                modifier = Modifier.weight(1f).clickable(onClick = onFotos),
                icon = Icons.Default.PhotoCamera,
                label = "Fotos",
                value = "${detalhe.totalFotos} foto(s)",
                filled = detalhe.totalFotos > 0
            )
            SubRecursoChip(
                modifier = Modifier.weight(1f).clickable(onClick = onOrcamentos),
                icon = Icons.Default.Receipt,
                label = "Orçamentos",
                value = "${detalhe.totalOrcamentos}",
                filled = detalhe.totalOrcamentos > 0
            )
        }
    }
}

@Composable
private fun OSInfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(text = title,   style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(text = content, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun OSDetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SubRecursoChip(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    filled: Boolean
) {
    val containerColor = if (filled) MaterialTheme.colorScheme.primaryContainer
                         else MaterialTheme.colorScheme.surfaceVariant
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
    }
}
