package br.com.carbuapp.ordens.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OSFormScreen(
    osId: Int? = null,
    veiculoId: Int? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: OSFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val categoriaValue    by viewModel.categoria.collectAsStateWithLifecycle()
    val descricaoValue    by viewModel.descricao.collectAsStateWithLifecycle()
    val dataServicoValue  by viewModel.dataServico.collectAsStateWithLifecycle()
    val observacoesValue  by viewModel.observacoes.collectAsStateWithLifecycle()

    val snackbarHost       = remember { SnackbarHostState() }
    var categoriaExpanded  by remember { mutableStateOf(false) }
    var showDatePicker     by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is UiState.Success -> onSaved()
            is UiState.Error   -> snackbarHost.showSnackbar(s.message)
            else -> Unit
        }
    }

    // ── DatePickerDialog ──────────────────────────────────────────────────────
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = runCatching {
                LocalDate.parse(dataServicoValue, DateTimeFormatter.ISO_LOCAL_DATE)
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant().toEpochMilli()
            }.getOrNull()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        viewModel.dataServico.value = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (osId == null) "Nova OS" else "Editar OS") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Categoria (dropdown) ─────────────────────────────────────────
            val categorias = listOf(
                "Revisão", "Elétrica", "Funilaria", "Mecânica",
                "Suspensão", "Freios", "Ar-condicionado", "Outro"
            )
            ExposedDropdownMenuBox(
                expanded = categoriaExpanded,
                onExpandedChange = { categoriaExpanded = !categoriaExpanded }
            ) {
                OutlinedTextField(
                    value = categoriaValue,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoriaExpanded,
                    onDismissRequest = { categoriaExpanded = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.categoria.value = cat
                                categoriaExpanded = false
                            }
                        )
                    }
                }
            }

            // ── Data do serviço — DatePicker ─────────────────────────────────
            OutlinedTextField(
                value = dataServicoValue,
                onValueChange = {},
                readOnly = true,
                label = { Text("Data do serviço *") },
                placeholder = { Text("Selecione a data") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Abrir calendário")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Descrição ────────────────────────────────────────────────────
            OutlinedTextField(
                value = descricaoValue,
                onValueChange = { viewModel.descricao.value = it },
                label = { Text("Descrição *") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Observações ──────────────────────────────────────────────────
            OutlinedTextField(
                value = observacoesValue,
                onValueChange = { viewModel.observacoes.value = it },
                label = { Text("Observações") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                    Text(if (osId == null) "Criar OS" else "Salvar alterações")
                }
            }
        }
    }
}
