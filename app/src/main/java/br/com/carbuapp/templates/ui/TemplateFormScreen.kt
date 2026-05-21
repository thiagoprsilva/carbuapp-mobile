package br.com.carbuapp.templates.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TemplateFormViewModel = hiltViewModel()
) {
    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val nome        by viewModel.nome.collectAsStateWithLifecycle()
    val itens       by viewModel.itens.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }
    val isEdit = viewModel.templateId != null

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
                title = { Text(if (isEdit) "Editar Template" else "Novo Template") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Error   -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
                else -> TemplateFormContent(
                    nome          = nome,
                    itens         = itens,
                    actionLoading = actionState is UiState.Loading,
                    isEdit        = isEdit,
                    onNomeChange  = viewModel::onNomeChange,
                    onAddItem     = viewModel::addItem,
                    onRemoveItem  = viewModel::removeItem,
                    onDescricao   = viewModel::updateDescricao,
                    onQtd         = viewModel::updateQtd,
                    onPreco       = viewModel::updatePreco,
                    onSave        = { viewModel.save(onSaved) }
                )
            }
        }
    }
}

@Composable
private fun TemplateFormContent(
    nome: String,
    itens: List<TemplateItemFormState>,
    actionLoading: Boolean,
    isEdit: Boolean,
    onNomeChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    onDescricao: (Int, String) -> Unit,
    onQtd: (Int, String) -> Unit,
    onPreco: (Int, String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (actionLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

        // ── Nome ─────────────────────────────────────────────────────────────
        OutlinedTextField(
            value = nome,
            onValueChange = onNomeChange,
            label = { Text("Nome do template") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // ── Itens ─────────────────────────────────────────────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Itens (${itens.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    FilledTonalButton(
                        onClick = onAddItem,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Adicionar")
                    }
                }

                itens.forEachIndexed { index, item ->
                    TemplateItemRow(
                        index    = index,
                        item     = item,
                        onRemove = { onRemoveItem(index) },
                        onDescricao = { onDescricao(index, it) },
                        onQtd       = { onQtd(index, it) },
                        onPreco     = { onPreco(index, it) }
                    )
                    if (index < itens.lastIndex) HorizontalDivider()
                }
            }
        }

        // ── Total estimado ────────────────────────────────────────────────────
        val total = itens.sumOf {
            (it.qtd.toIntOrNull() ?: 1) * (it.precoUnit.toDoubleOrNull() ?: 0.0)
        }
        Text(
            text = "Total estimado: R$ ${"%.2f".format(total)}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        // ── Salvar ────────────────────────────────────────────────────────────
        Button(
            onClick = onSave,
            enabled = !actionLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEdit) "Atualizar template" else "Salvar template")
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TemplateItemRow(
    index: Int,
    item: TemplateItemFormState,
    onRemove: () -> Unit,
    onDescricao: (String) -> Unit,
    onQtd: (String) -> Unit,
    onPreco: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Item ${index + 1}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remover", modifier = Modifier.size(18.dp))
            }
        }

        OutlinedTextField(
            value = item.descricao,
            onValueChange = onDescricao,
            label = { Text("Descrição do serviço") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = item.qtd,
                onValueChange = onQtd,
                label = { Text("Qtd") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = item.precoUnit,
                onValueChange = onPreco,
                label = { Text("Preço unit. (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(2f)
            )
        }
    }
}
