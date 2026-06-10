package br.com.carbuapp.oficina.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OficinaPerfilScreen(
    onBack: () -> Unit,
    viewModel: OficinaPerfilViewModel = hiltViewModel()
) {
    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val nome        by viewModel.nome.collectAsStateWithLifecycle()
    val responsavel by viewModel.responsavel.collectAsStateWithLifecycle()
    val telefone    by viewModel.telefone.collectAsStateWithLifecycle()
    val endereco    by viewModel.endereco.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.uploadLogo(it) } }

    var showDeleteLogoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        if (actionState is UiState.Error) {
            snackbarHost.showSnackbar((actionState as UiState.Error).message)
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil da Oficina") },
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load) { Text("Tentar novamente") }
                }
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (actionState is UiState.Loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    // ── Logo ────────────────────────────────────────────────────
                    val oficina = (state as? UiState.Success)?.data
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Logo da Oficina",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (oficina?.logoUrl != null) {
                                AsyncImage(
                                    model = "https://api.carbuapp.com.br/${oficina.logoUrl}",
                                    contentDescription = "Logo",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { imagePicker.launch("image/*") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Trocar")
                                    }
                                    OutlinedButton(
                                        onClick = { showDeleteLogoDialog = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Remover")
                                    }
                                }
                            } else {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth().height(80.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            "Sem logo",
                                            color = MaterialTheme.colorScheme.outline,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                FilledTonalButton(
                                    onClick = { imagePicker.launch("image/*") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Enviar logo")
                                }
                            }
                        }
                    }

                    // ── Dados da oficina ─────────────────────────────────────────
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Dados da Oficina",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            OutlinedTextField(
                                value = nome,
                                onValueChange = { viewModel.nome.value = it },
                                label = { Text("Nome") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = responsavel,
                                onValueChange = { viewModel.responsavel.value = it },
                                label = { Text("Responsável") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = telefone,
                                onValueChange = { viewModel.telefone.value = it },
                                label = { Text("Telefone") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = endereco,
                                onValueChange = { viewModel.endereco.value = it },
                                label = { Text("Endereço") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.save(onBack) },
                        enabled = actionState !is UiState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salvar alterações")
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    if (showDeleteLogoDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteLogoDialog = false },
            title = { Text("Remover logo") },
            text  = { Text("Tem certeza que deseja remover o logo da oficina?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteLogoDialog = false
                    viewModel.deleteLogo()
                }) { Text("Remover", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteLogoDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
