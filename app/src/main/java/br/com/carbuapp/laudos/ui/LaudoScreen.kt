package br.com.carbuapp.laudos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import br.com.carbuapp.laudos.domain.model.NIVEIS_COMBUSTIVEL
import br.com.carbuapp.laudos.domain.model.SEVERIDADES
import br.com.carbuapp.laudos.domain.model.ZONAS_VEICULO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaudoScreen(
    onBack: () -> Unit,
    viewModel: LaudoViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState   by viewModel.actionState.collectAsStateWithLifecycle()
    val km            by viewModel.km.collectAsStateWithLifecycle()
    val nivelCombust  by viewModel.nivelCombust.collectAsStateWithLifecycle()
    val observacoes   by viewModel.observacoes.collectAsStateWithLifecycle()
    val avarias       by viewModel.avarias.collectAsStateWithLifecycle()
    val laudoExiste   by viewModel.laudoExistente.collectAsStateWithLifecycle()

    val snackbarHost      = remember { SnackbarHostState() }
    var showDeleteDialog  by remember { mutableStateOf(false) }

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
                title = { Text("Laudo de Entrada") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (laudoExiste && uiState is UiState.Success) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir laudo")
                        }
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load) { Text("Tentar novamente") }
                }
                else -> LaudoFormContent(
                    km           = km,
                    nivelCombust = nivelCombust,
                    observacoes  = observacoes,
                    avarias      = avarias,
                    actionLoading = actionState is UiState.Loading,
                    laudoExiste  = laudoExiste,
                    onKmChange           = viewModel::onKmChange,
                    onNivelChange        = viewModel::onNivelChange,
                    onObservacoesChange  = viewModel::onObservacoesChange,
                    onAddAvaria          = viewModel::addAvaria,
                    onRemoveAvaria       = viewModel::removeAvaria,
                    onAvariaZonaChange   = viewModel::updateAvariaZona,
                    onAvariaSeveridade   = viewModel::updateAvariaSeveridade,
                    onAvariaObservacao   = viewModel::updateAvariaObservacao,
                    onSave               = { viewModel.save(onSaved = onBack) }
                )
            }
        }
    }

    // ── Diálogo: confirmar exclusão ──────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Laudo") },
            text  = { Text("Tem certeza que deseja excluir o laudo desta OS? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.delete(onDeleted = onBack)
                    }
                ) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun LaudoFormContent(
    km: String,
    nivelCombust: String?,
    observacoes: String,
    avarias: List<AvariaFormState>,
    actionLoading: Boolean,
    laudoExiste: Boolean,
    onKmChange: (String) -> Unit,
    onNivelChange: (String?) -> Unit,
    onObservacoesChange: (String) -> Unit,
    onAddAvaria: () -> Unit,
    onRemoveAvaria: (Int) -> Unit,
    onAvariaZonaChange: (Int, String) -> Unit,
    onAvariaSeveridade: (Int, String?) -> Unit,
    onAvariaObservacao: (Int, String) -> Unit,
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

        // ── Dados gerais ─────────────────────────────────────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Dados do veículo na entrada",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = km,
                    onValueChange = onKmChange,
                    label = { Text("Quilometragem (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                LaudoDropdown(
                    label = "Nível de combustível",
                    selected = nivelCombust,
                    options = NIVEIS_COMBUSTIVEL,
                    allowClear = true,
                    onSelect = onNivelChange
                )

                OutlinedTextField(
                    value = observacoes,
                    onValueChange = onObservacoesChange,
                    label = { Text("Observações gerais") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── Mapa de avarias ──────────────────────────────────────────────────
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
                        text = "Avarias (${avarias.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    FilledTonalButton(
                        onClick = onAddAvaria,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Adicionar")
                    }
                }

                if (avarias.isEmpty()) {
                    Text(
                        text = "Nenhuma avaria registrada.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    avarias.forEachIndexed { index, avaria ->
                        AvariaFormCard(
                            index      = index,
                            avaria     = avaria,
                            onRemove   = { onRemoveAvaria(index) },
                            onZona     = { onAvariaZonaChange(index, it) },
                            onSeveri   = { onAvariaSeveridade(index, it) },
                            onObserv   = { onAvariaObservacao(index, it) }
                        )
                        if (index < avarias.lastIndex) HorizontalDivider()
                    }
                }
            }
        }

        // ── Botão salvar ─────────────────────────────────────────────────────
        Button(
            onClick = onSave,
            enabled = !actionLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (laudoExiste) "Atualizar laudo" else "Salvar laudo")
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AvariaFormCard(
    index: Int,
    avaria: AvariaFormState,
    onRemove: () -> Unit,
    onZona: (String) -> Unit,
    onSeveri: (String?) -> Unit,
    onObserv: (String) -> Unit
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
                text = "Avaria ${index + 1}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remover avaria", modifier = Modifier.size(18.dp))
            }
        }

        LaudoDropdown(
            label = "Zona do veículo",
            selected = avaria.zona.ifBlank { null },
            options = ZONAS_VEICULO,
            allowClear = false,
            onSelect = { onZona(it ?: "") }
        )

        LaudoDropdown(
            label = "Severidade",
            selected = avaria.severidade,
            options = SEVERIDADES,
            allowClear = true,
            onSelect = onSeveri
        )

        OutlinedTextField(
            value = avaria.observacao,
            onValueChange = onObserv,
            label = { Text("Observação") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaudoDropdown(
    label: String,
    selected: String?,
    options: List<String>,
    allowClear: Boolean,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (allowClear && selected != null) {
                DropdownMenuItem(
                    text = { Text("— Limpar —", color = MaterialTheme.colorScheme.outline) },
                    onClick = {
                        onSelect(null)
                        expanded = false
                    }
                )
                HorizontalDivider()
            }
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
