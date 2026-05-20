package br.com.carbuapp.veiculos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.veiculos.domain.model.Veiculo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculoListScreen(
    onVeiculoClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    showBackButton: Boolean = false,
    onBack: () -> Unit = {},
    viewModel: VeiculoListViewModel = hiltViewModel()
) {
    val veiculos by viewModel.veiculos.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val refreshState by viewModel.refreshState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }
    var veiculoToDelete by remember { mutableStateOf<Veiculo?>(null) }

    LaunchedEffect(deleteState) {
        if (deleteState is UiState.Error) {
            snackbarHost.showSnackbar((deleteState as UiState.Error).message)
            viewModel.clearDeleteState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (viewModel.clienteId != null) "Veículos do Cliente"
                        else "Veículos"
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Novo veículo")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = refreshState is UiState.Loading,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchChange,
                    placeholder = { Text("Buscar por placa ou modelo...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (veiculos.isEmpty() && refreshState !is UiState.Loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isBlank()) "Nenhum veículo cadastrado"
                                       else "Nenhum resultado para \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn {
                        items(veiculos, key = { it.id }) { veiculo ->
                            VeiculoItem(
                                veiculo = veiculo,
                                onClick = { onVeiculoClick(veiculo.id) },
                                onDeleteClick = { veiculoToDelete = veiculo }
                            )
                        }
                    }
                }
            }
        }
    }

    veiculoToDelete?.let { veiculo ->
        AlertDialog(
            onDismissRequest = { veiculoToDelete = null },
            title = { Text("Excluir veículo") },
            text = { Text("Excluir ${veiculo.placa} — ${veiculo.modelo}? Só é possível se não houver OS ou orçamentos vinculados.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(veiculo.id)
                    veiculoToDelete = null
                }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { veiculoToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun VeiculoItem(
    veiculo: Veiculo,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${veiculo.placa}  ·  ${veiculo.modelo}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(2.dp))
                val detalhes = listOfNotNull(veiculo.ano, veiculo.motor, veiculo.alimentacao)
                    .joinToString(" · ")
                if (detalhes.isNotBlank()) {
                    Text(
                        text = detalhes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!veiculo.clienteNome.isNullOrBlank()) {
                    Text(
                        text = veiculo.clienteNome,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
