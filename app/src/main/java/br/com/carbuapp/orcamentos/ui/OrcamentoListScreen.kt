package br.com.carbuapp.orcamentos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
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
import br.com.carbuapp.orcamentos.domain.model.Orcamento
import br.com.carbuapp.orcamentos.domain.model.STATUS_ORCAMENTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrcamentoListScreen(
    onOrcamentoClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    viewModel: OrcamentoListViewModel = hiltViewModel()
) {
    val orcamentos by viewModel.orcamentos.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
    val refreshState by viewModel.refreshState.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(refreshState) {
        if (refreshState is UiState.Error)
            snackbarHost.showSnackbar((refreshState as UiState.Error).message)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Orçamentos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Novo orçamento")
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

                // ── Filter chips ─────────────────────────────────────────────
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = statusFilter == null,
                            onClick = { viewModel.setStatusFilter(null) },
                            label = { Text("Todos") }
                        )
                    }
                    items(STATUS_ORCAMENTO) { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { viewModel.setStatusFilter(status) },
                            label = { Text(status) }
                        )
                    }
                }

                HorizontalDivider()

                if (orcamentos.isEmpty() && refreshState !is UiState.Loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Receipt, null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = if (statusFilter != null) "Nenhum orçamento \"$statusFilter\""
                                       else "Nenhum orçamento cadastrado",
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(orcamentos, key = { it.id }) { orc ->
                            OrcamentoCard(orc = orc, onClick = { onOrcamentoClick(orc.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrcamentoCard(orc: Orcamento, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Orç. #${orc.numero} · OS #${orc.osNumero}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${orc.placa} · ${orc.modelo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = orc.clienteNome,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OrcamentoStatusChip(status = orc.status)
                Text(
                    text = "R$ ${"%.2f".format(orc.total)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

