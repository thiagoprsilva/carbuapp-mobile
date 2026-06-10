package br.com.carbuapp.usuarios.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.usuarios.domain.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: UsuariosViewModel = hiltViewModel()
) {
    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    // Estado para dialog de reset de senha
    var resetTarget by remember { mutableStateOf<Usuario?>(null) }
    var novaSenha   by remember { mutableStateOf("") }

    LaunchedEffect(actionState) {
        when (val s = actionState) {
            is UiState.Error   -> {
                snackbarHost.showSnackbar(s.message)
                viewModel.resetActionState()
            }
            is UiState.Success -> {
                if (resetTarget != null) {
                    snackbarHost.showSnackbar("Senha resetada com sucesso.")
                    resetTarget = null
                    novaSenha   = ""
                }
                viewModel.resetActionState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuários") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Novo usuário")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState is UiState.Loading,
            onRefresh    = viewModel::load,
            modifier     = Modifier.fillMaxSize().padding(padding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                is UiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = viewModel::load) { Text("Tentar novamente") }
                    }
                }
                is UiState.Empty -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Nenhum usuário cadastrado.", color = MaterialTheme.colorScheme.outline)
                }
                is UiState.Success -> {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(state.data) { usuario ->
                            UsuarioItem(
                                usuario   = usuario,
                                onEdit    = { onEditClick(usuario.id) },
                                onReset   = { resetTarget = usuario }
                            )
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    // ── Dialog reset de senha ────────────────────────────────────────────────
    resetTarget?.let { usuario ->
        AlertDialog(
            onDismissRequest = { resetTarget = null; novaSenha = "" },
            title = { Text("Resetar senha") },
            text  = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nova senha para ${usuario.nome}:")
                    OutlinedTextField(
                        value = novaSenha,
                        onValueChange = { novaSenha = it },
                        label = { Text("Nova senha") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (novaSenha.length >= 6) {
                            viewModel.resetarSenha(usuario.id, novaSenha) {}
                        }
                    },
                    enabled = novaSenha.length >= 6 && actionState !is UiState.Loading
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { resetTarget = null; novaSenha = "" }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun UsuarioItem(
    usuario: Usuario,
    onEdit: () -> Unit,
    onReset: () -> Unit
) {
    ListItem(
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(usuario.nome, fontWeight = FontWeight.SemiBold)
                if (!usuario.ativo) {
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            "Inativo",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        },
        supportingContent = {
            Column {
                Text(usuario.email, style = MaterialTheme.typography.bodySmall)
                RoleBadge(usuario.role)
            }
        },
        leadingContent = {
            Icon(Icons.Default.Person, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Row {
                IconButton(onClick = onReset) {
                    Icon(Icons.Default.LockReset, contentDescription = "Resetar senha",
                        tint = MaterialTheme.colorScheme.outline)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    )
    HorizontalDivider()
}

@Composable
private fun RoleBadge(role: String) {
    val (label, color) = when (role) {
        "SUPERADMIN" -> "Super Admin" to MaterialTheme.colorScheme.errorContainer
        "ADMIN"      -> "Admin" to MaterialTheme.colorScheme.tertiaryContainer
        else         -> "Mecânico" to MaterialTheme.colorScheme.surfaceVariant
    }
    Spacer(Modifier.height(2.dp))
    Surface(shape = MaterialTheme.shapes.extraSmall, color = color) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
