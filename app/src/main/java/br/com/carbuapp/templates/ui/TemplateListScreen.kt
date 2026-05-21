package br.com.carbuapp.templates.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.templates.domain.model.Template

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateListScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: TemplateListViewModel = hiltViewModel()
) {
    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    val snackbarHost       = remember { SnackbarHostState() }
    var templateParaDeletar by remember { mutableStateOf<Template?>(null) }

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
                title = { Text("Templates de Serviço") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Novo template")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (actionState is UiState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }

            when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is UiState.Empty -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null,
                        modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Text(
                        "Nenhum template cadastrado.\nToque em + para criar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }

                is UiState.Error -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load) { Text("Tentar novamente") }
                }

                is UiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.data, key = { it.id }) { template ->
                        TemplateCard(
                            template    = template,
                            onEdit      = { onEditClick(template.id) },
                            onDelete    = { templateParaDeletar = template }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }

                else -> Unit
            }
        }
    }

    templateParaDeletar?.let { t ->
        AlertDialog(
            onDismissRequest = { templateParaDeletar = null },
            title = { Text("Excluir template") },
            text  = { Text("Tem certeza que deseja excluir \"${t.nome}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    templateParaDeletar = null
                    viewModel.delete(t.id)
                }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { templateParaDeletar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun TemplateCard(
    template: Template,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = template.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir",
                            modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Text(
                text = "${template.itens.size} item(s) · Total: R$ ${"%.2f".format(template.itens.sumOf { it.qtd * it.precoUnit })}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            if (template.itens.isNotEmpty()) {
                HorizontalDivider()
                template.itens.take(3).forEach { item ->
                    Text(
                        text = "• ${item.descricao} (${item.qtd}x · R$ ${"%.2f".format(item.precoUnit)})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (template.itens.size > 3) {
                    Text(
                        text = "+ ${template.itens.size - 3} item(s)...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
