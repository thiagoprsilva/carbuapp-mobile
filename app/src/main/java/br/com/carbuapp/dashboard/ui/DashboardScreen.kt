package br.com.carbuapp.dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.dashboard.domain.OrcamentoRecente
import br.com.carbuapp.dashboard.domain.OSRecente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onOSClick: (Int) -> Unit = {},
    onOrcamentoClick: (Int) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState is UiState.Loading,
            onRefresh = viewModel::load,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                is UiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = viewModel::load) { Text("Tentar novamente") }
                    }
                }
                is UiState.Success -> {
                    val summary = state.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // ── Totais ──────────────────────────────────────────────
                        Text("Visão Geral", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TotalCard(modifier = Modifier.weight(1f), label = "Clientes",   value = summary.totais.clientes,   icon = Icons.Default.Person,       color = MaterialTheme.colorScheme.primaryContainer)
                            TotalCard(modifier = Modifier.weight(1f), label = "Veículos",   value = summary.totais.veiculos,   icon = Icons.Default.DirectionsCar, color = MaterialTheme.colorScheme.secondaryContainer)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TotalCard(modifier = Modifier.weight(1f), label = "OS",         value = summary.totais.registros,  icon = Icons.Default.Build,        color = MaterialTheme.colorScheme.tertiaryContainer)
                            TotalCard(modifier = Modifier.weight(1f), label = "Orçamentos", value = summary.totais.orcamentos, icon = Icons.Default.Receipt,       color = MaterialTheme.colorScheme.errorContainer)
                        }

                        // ── Últimas OS ──────────────────────────────────────────
                        if (summary.recentes.registros.isNotEmpty()) {
                            HorizontalDivider()
                            Text("Últimas OS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            summary.recentes.registros.forEach { os ->
                                OSRecenteCard(os = os, onClick = { onOSClick(os.id) })
                            }
                        }

                        // ── Últimos Orçamentos ──────────────────────────────────
                        if (summary.recentes.orcamentos.isNotEmpty()) {
                            HorizontalDivider()
                            Text("Últimos Orçamentos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            summary.recentes.orcamentos.forEach { orc ->
                                OrcamentoRecenteCard(orc = orc, onClick = { onOrcamentoClick(orc.id) })
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun TotalCard(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(28.dp))
            Text(text = value.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun OSRecenteCard(os: OSRecente, onClick: () -> Unit) {
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
                    text = "OS #${os.numero} · ${os.placa}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(text = os.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = os.clienteNome, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusChip(status = os.status)
        }
    }
}

@Composable
private fun OrcamentoRecenteCard(orc: OrcamentoRecente, onClick: () -> Unit) {
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
                Text(text = "Orç. #${orc.numero} · ${orc.placa}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(text = orc.clienteNome, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = orc.createdAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "R$ ${"%.2f".format(orc.total)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (containerColor, contentColor) = when (status) {
        "Aberta"           -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "Em andamento"     -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "Aguardando peças" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "Concluída"        -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        "Cancelada"        -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else               -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
    }
    Surface(shape = MaterialTheme.shapes.small, color = containerColor) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}
