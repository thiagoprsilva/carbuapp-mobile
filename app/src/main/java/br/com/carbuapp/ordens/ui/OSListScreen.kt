package br.com.carbuapp.ordens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.dashboard.ui.StatusChip
import br.com.carbuapp.ordens.domain.model.OrdemServico
import br.com.carbuapp.ordens.domain.model.STATUS_OS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OSListScreen(
    onOSClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    viewModel: OSListViewModel = hiltViewModel()
) {
    val ordens by viewModel.ordens.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
    val refreshState by viewModel.refreshState.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(refreshState) {
        if (refreshState is UiState.Error) {
            snackbarHost.showSnackbar((refreshState as UiState.Error).message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ordens de Serviço") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Nova OS")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = refreshState is UiState.Loading,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Status filter chips ─────────────────────────────────────
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "Todos"
                    item {
                        FilterChip(
                            selected = statusFilter == null,
                            onClick = { viewModel.setStatusFilter(null) },
                            label = { Text("Todos") }
                        )
                    }
                    items(STATUS_OS) { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { viewModel.setStatusFilter(status) },
                            label = { Text(status) }
                        )
                    }
                }

                HorizontalDivider()

                if (ordens.isEmpty() && refreshState !is UiState.Loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = if (statusFilter != null) "Nenhuma OS com status \"$statusFilter\""
                                       else "Nenhuma OS cadastrada",
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ordens, key = { it.id }) { os ->
                            OSCard(os = os, onClick = { onOSClick(os.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OSCard(os: OrdemServico, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "OS #${os.numero} · ${os.placa}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = os.modelo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = os.clienteNome,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = os.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            StatusChip(status = os.status)
        }
    }
}
