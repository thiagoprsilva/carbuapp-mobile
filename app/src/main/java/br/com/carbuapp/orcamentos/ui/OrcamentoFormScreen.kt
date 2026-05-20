package br.com.carbuapp.orcamentos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrcamentoFormScreen(
    orcamentoId: Int? = null,
    osId: Int? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: OrcamentoFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val itens by viewModel.itens.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is UiState.Success -> onSaved()
            is UiState.Error   -> snackbarHost.showSnackbar(s.message)
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (orcamentoId == null) "Novo Orçamento" else "Editar Orçamento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Cabeçalho ──────────────────────────────────────────────────
            item {
                Text(
                    text = "Itens do orçamento",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Adicione os serviços e peças. Qtd e preço unitário são opcionais.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // ── Linhas de item ──────────────────────────────────────────────
            itemsIndexed(itens) { index, item ->
                ItemFormRow(
                    index  = index,
                    item   = item,
                    total  = itens.size,
                    onUpdate  = { viewModel.updateItem(index, it) },
                    onRemove  = { viewModel.removeItem(index) }
                )
            }

            // ── Botão adicionar item ────────────────────────────────────────
            item {
                OutlinedButton(
                    onClick = viewModel::addItem,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Adicionar item")
                }
            }

            // ── Total calculado ─────────────────────────────────────────────
            item {
                val total = itens.sumOf {
                    (it.qtd.toDoubleOrNull() ?: 0.0) * (it.precoUnit.toDoubleOrNull() ?: 0.0)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total estimado", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "R$ ${"%.2f".format(total)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // ── Salvar ──────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = viewModel::save,
                    enabled = uiState !is UiState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (orcamentoId == null) "Criar orçamento" else "Salvar alterações")
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemFormRow(
    index: Int,
    item: ItemFormState,
    total: Int,
    onUpdate: (ItemFormState) -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Item ${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                if (total > 1) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, "Remover item",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp))
                    }
                }
            }
            OutlinedTextField(
                value = item.descricao,
                onValueChange = { onUpdate(item.copy(descricao = it)) },
                label = { Text("Descrição *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = item.qtd,
                    onValueChange = { onUpdate(item.copy(qtd = it)) },
                    label = { Text("Qtd") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = item.precoUnit,
                    onValueChange = { onUpdate(item.copy(precoUnit = it)) },
                    label = { Text("Preço unit.") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
            val subtotal = (item.qtd.toDoubleOrNull() ?: 0.0) * (item.precoUnit.toDoubleOrNull() ?: 0.0)
            if (subtotal > 0) {
                Text(
                    "Subtotal: R$ ${"%.2f".format(subtotal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
