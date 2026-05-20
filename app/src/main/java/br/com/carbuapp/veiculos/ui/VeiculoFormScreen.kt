package br.com.carbuapp.veiculos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculoFormScreen(
    veiculoId: Int?,
    clienteId: Int?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: VeiculoFormViewModel = hiltViewModel()
) {
    val loadState by viewModel.loadState.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    val placa by viewModel.placa.collectAsStateWithLifecycle()
    val modelo by viewModel.modelo.collectAsStateWithLifecycle()
    val ano by viewModel.ano.collectAsStateWithLifecycle()
    val motor by viewModel.motor.collectAsStateWithLifecycle()
    val alimentacao by viewModel.alimentacao.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }
    var showAlimentacaoMenu by remember { mutableStateOf(false) }

    LaunchedEffect(saveState) {
        when (saveState) {
            is UiState.Success -> onSaved()
            is UiState.Error   -> snackbarHost.showSnackbar((saveState as UiState.Error).message)
            else               -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Editar Veículo" else "Novo Veículo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        when (loadState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = placa,
                        onValueChange = { viewModel.placa.value = it.uppercase() },
                        label = { Text("Placa *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(KeyboardCapitalization.Characters),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = modelo,
                        onValueChange = { viewModel.modelo.value = it },
                        label = { Text("Modelo *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ano,
                        onValueChange = { viewModel.ano.value = it },
                        label = { Text("Ano") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = motor,
                        onValueChange = { viewModel.motor.value = it },
                        label = { Text("Motor") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Dropdown para alimentação
                    ExposedDropdownMenuBox(
                        expanded = showAlimentacaoMenu,
                        onExpandedChange = { showAlimentacaoMenu = it }
                    ) {
                        OutlinedTextField(
                            value = alimentacao,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sistema de alimentação") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showAlimentacaoMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showAlimentacaoMenu,
                            onDismissRequest = { showAlimentacaoMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("(Nenhum)") },
                                onClick = { viewModel.alimentacao.value = ""; showAlimentacaoMenu = false }
                            )
                            viewModel.alimentacaoOpcoes.forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao) },
                                    onClick = { viewModel.alimentacao.value = opcao; showAlimentacaoMenu = false }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = viewModel::save,
                        enabled = saveState !is UiState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (saveState is UiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text(if (viewModel.isEditing) "Salvar alterações" else "Criar veículo")
                        }
                    }
                }
            }
        }
    }
}
