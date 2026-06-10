package br.com.carbuapp.menu.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.auth.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onLogout: () -> Unit,
    onTrocarOficina: () -> Unit,
    onTemplates: () -> Unit = {},
    onOficina: () -> Unit = {},
    onUsuarios: () -> Unit = {},
    viewModel: MenuViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Menu") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Cartão do usuário ─────────────────────────────────────────
            user?.let { u ->
                UserCard(user = u)
                Spacer(Modifier.height(8.dp))
            }

            // ── Seção: Ferramentas ────────────────────────────────────────
            MenuSectionHeader("Ferramentas")
            MenuItemRow(
                icon  = Icons.Default.Build,
                label = "Templates de Serviço",
                onClick = onTemplates
            )

            // ── Seção: Administração (somente admin/superadmin) ───────────
            if (user?.isAdmin == true) {
                Spacer(Modifier.height(4.dp))
                MenuSectionHeader("Administração")
                MenuItemRow(
                    icon  = Icons.Default.Store,
                    label = "Perfil da Oficina",
                    onClick = onOficina
                )
                MenuItemRow(
                    icon  = Icons.Default.Group,
                    label = "Usuários",
                    onClick = onUsuarios
                )
            }

            // ── Seção: Conta ──────────────────────────────────────────────
            MenuSectionHeader("Conta")

            // Trocar oficina — visível apenas para superadmin
            if (user?.isSuperAdmin == true) {
                MenuItemRow(
                    icon  = Icons.Default.SwapHoriz,
                    label = "Trocar oficina",
                    tint  = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        viewModel.trocarOficina(onTrocarOficina)
                    }
                )
            }

            MenuItemRow(
                icon    = Icons.Default.Logout,
                label   = "Sair",
                tint    = MaterialTheme.colorScheme.error,
                onClick = { showLogoutDialog = true }
            )

            Spacer(Modifier.weight(1f))

            // ── Versão ────────────────────────────────────────────────────
            Text(
                text = "CarbuApp Mobile v1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    // ── Confirmar logout ─────────────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sair") },
            text  = { Text("Tem certeza que deseja sair da conta?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout(onLoggedOut = onLogout)
                }) {
                    Text("Sair", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun UserCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.nome.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column {
                Text(
                    text = user.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                RoleBadge(role = user.role)
            }
        }
    }
}

@Composable
private fun RoleBadge(role: String) {
    val (label, containerColor) = when (role) {
        "SUPERADMIN" -> "Super Admin" to MaterialTheme.colorScheme.errorContainer
        "ADMIN"      -> "Administrador" to MaterialTheme.colorScheme.tertiaryContainer
        else         -> "Usuário" to MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(shape = MaterialTheme.shapes.small, color = containerColor) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun MenuSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun MenuItemRow(
    icon: ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = tint)
        }
    }
}
