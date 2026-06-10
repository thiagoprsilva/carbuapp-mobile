package br.com.carbuapp.usuarios.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: UsuarioFormViewModel = hiltViewModel()
) {
    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val nome        by viewModel.nome.collectAsStateWithLifecycle()
    val email       by viewModel.email.collectAsStateWithLifecycle()
    val senha       by viewModel.senha.collectAsStateWithLifecycle()
    val role        by viewModel.role.collectAsStateWithLifecycle()
    val ativo       by viewModel.ativo.collectAsStateWithLifecycle()

    val snackbarHost   = remember { SnackbarHostState() }
    var senhaVisible   by remember { mutableStateOf(false) }
    var roleExpanded   by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        if (actionState is UiState.Error) {
            snackbarHost.showSnackbar((actionState as UiState.Error).message)
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEdit) "Editar Usuário" else "Novo Usuário") },
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

                    // ── Nome ────────────────────────────────────────────────────
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { viewModel.nome.value = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ── E-mail ──────────────────────────────────────────────────
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.email.value = it },
                        label = { Text("E-mail") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ── Senha (somente novo usuário) ────────────────────────────
                    if (!viewModel.isEdit) {
                        OutlinedTextField(
                            value = senha,
                            onValueChange = { viewModel.senha.value = it },
                            label = { Text("Senha (mín. 6 caracteres)") },
                            singleLine = true,
                            visualTransformation = if (senhaVisible) VisualTransformation.None
                                                   else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { senhaVisible = !senhaVisible }) {
                                    Icon(
                                        if (senhaVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // ── Perfil (role) ───────────────────────────────────────────
                    ExposedDropdownMenuBox(
                        expanded = roleExpanded,
                        onExpandedChange = { roleExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = roleLabel(role),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Perfil") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = roleExpanded,
                            onDismissRequest = { roleExpanded = false }
                        ) {
                            listOf("MECANICO", "ADMIN").forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(roleLabel(r)) },
                                    onClick = {
                                        viewModel.role.value = r
                                        roleExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // ── Ativo (somente edição) ──────────────────────────────────
                    if (viewModel.isEdit) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Usuário ativo")
                            Switch(
                                checked = ativo,
                                onCheckedChange = { viewModel.ativo.value = it }
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.save(onSaved) },
                        enabled = actionState !is UiState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (viewModel.isEdit) "Salvar alterações" else "Criar usuário")
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

private fun roleLabel(role: String) = when (role) {
    "ADMIN"   -> "Administrador"
    "MECANICO" -> "Mecânico"
    else       -> role
}
