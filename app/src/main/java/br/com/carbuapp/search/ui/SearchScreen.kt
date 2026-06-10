package br.com.carbuapp.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.search.domain.SearchResult
import br.com.carbuapp.search.domain.SearchResultType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onClienteClick: (Int) -> Unit,
    onVeiculoClick: (Int) -> Unit,
    onOrcamentoClick: (Int) -> Unit,
    onOSClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query   by viewModel.query.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = viewModel::onQueryChange,
                        placeholder = { Text("Buscar clientes, veículos, OS...") },
                        singleLine = true,
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = viewModel::clearQuery) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpar")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is UiState.Idle -> SearchIdle()
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Empty -> SearchEmpty(query)
                is UiState.Error -> Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
                is UiState.Success -> SearchResults(
                    results = state.data,
                    onClienteClick  = onClienteClick,
                    onVeiculoClick  = onVeiculoClick,
                    onOrcamentoClick = onOrcamentoClick,
                    onOSClick       = onOSClick
                )
            }
        }
    }
}

@Composable
private fun SearchIdle() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Digite ao menos 2 caracteres para buscar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SearchEmpty(query: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Nenhum resultado para \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SearchResults(
    results: List<SearchResult>,
    onClienteClick: (Int) -> Unit,
    onVeiculoClick: (Int) -> Unit,
    onOrcamentoClick: (Int) -> Unit,
    onOSClick: (Int) -> Unit
) {
    val grouped = results.groupBy { it.type }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        SearchResultType.entries.forEach { type ->
            val items = grouped[type] ?: return@forEach
            item {
                Text(
                    text = type.label(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(items) { result ->
                SearchResultItem(
                    result = result,
                    onClick = {
                        when (result.type) {
                            SearchResultType.CLIENTE   -> onClienteClick(result.id)
                            SearchResultType.VEICULO   -> onVeiculoClick(result.id)
                            SearchResultType.ORCAMENTO -> onOrcamentoClick(result.id)
                            SearchResultType.REGISTRO  -> onOSClick(result.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(result.title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { if (result.subtitle.isNotEmpty()) Text(result.subtitle) },
        leadingContent = {
            Icon(
                imageVector = result.type.icon(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.outline)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
}

private fun SearchResultType.label() = when (this) {
    SearchResultType.CLIENTE   -> "Clientes"
    SearchResultType.VEICULO   -> "Veículos"
    SearchResultType.ORCAMENTO -> "Orçamentos"
    SearchResultType.REGISTRO  -> "Ordens de Serviço"
}

private fun SearchResultType.icon(): ImageVector = when (this) {
    SearchResultType.CLIENTE   -> Icons.Default.Person
    SearchResultType.VEICULO   -> Icons.Default.DirectionsCar
    SearchResultType.ORCAMENTO -> Icons.Default.Receipt
    SearchResultType.REGISTRO  -> Icons.Default.Build
}
