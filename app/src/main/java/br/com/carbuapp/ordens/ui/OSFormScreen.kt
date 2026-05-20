package br.com.carbuapp.ordens.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.ordens.domain.model.STATUS_OS

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

    val categoriaValue by viewModel.categoria.collectAsStateWithLifecycle()
    val descricaoValue by viewModel.descricao.collectAsStateWithLifecycle()
    val dataServicoValue by viewModel.dataServico.collectAsStateWithLifecycle()
    val observacoesValue by viewModel.observacoes.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }
    var categoriaExpanded by remember { mutableStateOf(false) }

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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
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

            // ── Data do serviço ──────────────────────────────────────────────
            OutlinedTextField(
                value = dataServicoValue,
                onValueChange = { viewModel.dataServico.value = it },
                label = { Text("Data do serviço * (AAAA-MM-DD)") },
                placeholder = { Text("ex: 2025-01-20") },
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
