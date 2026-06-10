package br.com.carbuapp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.carbuapp.clientes.ui.ClienteDetailScreen
import br.com.carbuapp.clientes.ui.ClienteFormScreen
import br.com.carbuapp.clientes.ui.ClienteListScreen
import br.com.carbuapp.dashboard.ui.DashboardScreen
import br.com.carbuapp.fotos.ui.FotoGalleryScreen
import br.com.carbuapp.laudos.ui.LaudoScreen
import br.com.carbuapp.menu.ui.MenuScreen
import br.com.carbuapp.orcamentos.ui.OrcamentoDetailScreen
import br.com.carbuapp.orcamentos.ui.OrcamentoFormScreen
import br.com.carbuapp.orcamentos.ui.OrcamentoListScreen
import br.com.carbuapp.ordens.ui.EntradaRapidaScreen
import br.com.carbuapp.ordens.ui.OSDetailScreen
import br.com.carbuapp.ordens.ui.OSFormScreen
import br.com.carbuapp.ordens.ui.OSListScreen
import br.com.carbuapp.oficina.ui.OficinaPerfilScreen
import br.com.carbuapp.search.ui.SearchScreen
import br.com.carbuapp.templates.ui.TemplateFormScreen
import br.com.carbuapp.templates.ui.TemplateListScreen
import br.com.carbuapp.usuarios.ui.UsuarioFormScreen
import br.com.carbuapp.usuarios.ui.UsuariosScreen
import br.com.carbuapp.veiculos.ui.VeiculoDetailScreen
import br.com.carbuapp.veiculos.ui.VeiculoFormScreen
import br.com.carbuapp.veiculos.ui.VeiculoListScreen
import javax.inject.Inject

// ViewModel auxiliar para observar conectividade no Composable
import androidx.lifecycle.ViewModel
import br.com.carbuapp.core.connectivity.ConnectivityObserver as IConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    connectivityObserver: IConnectivityObserver
) : ViewModel() {
    val isOnline: StateFlow<Boolean> = connectivityObserver.isOnline
}

@Composable
fun OfflineBanner() {
    val viewModel: ConnectivityViewModel = hiltViewModel()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = !isOnline,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.WifiOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Sem conexão com a internet",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = mainNavController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OfflineBanner()

            NavHost(
                navController = mainNavController,
                startDestination = Routes.Dashboard.route,
                modifier = Modifier.weight(1f)
            ) {
            // ── Dashboard ──────────────────────────────────────────────────────
            composable(Routes.Dashboard.route) {
                DashboardScreen(
                    onOSClick        = { osId  -> mainNavController.navigate(Routes.OSDetail.createRoute(osId)) },
                    onOrcamentoClick = { orcId -> mainNavController.navigate(Routes.OrcamentoDetail.createRoute(orcId)) },
                    onSearchClick    = { mainNavController.navigate(Routes.Search.route) },
                    onNovaOS         = { mainNavController.navigate(Routes.EntradaRapida.route) }
                )
            }

            // ── Clientes ───────────────────────────────────────────────────────
            composable(Routes.Clientes.route) {
                ClienteListScreen(
                    onClienteClick = { id -> mainNavController.navigate(Routes.ClienteDetail.createRoute(id)) },
                    onAddClick     = { mainNavController.navigate(Routes.ClienteForm.createRoute()) }
                )
            }
            composable(
                route = Routes.ClienteDetail.route,
                arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
            ) { back ->
                val clienteId = back.arguments!!.getInt("clienteId")
                ClienteDetailScreen(
                    clienteId = clienteId,
                    onBack    = { mainNavController.popBackStack() },
                    onEdit    = { mainNavController.navigate(Routes.ClienteForm.createRoute(clienteId)) }
                )
            }
            composable(
                route = Routes.ClienteForm.route,
                arguments = listOf(navArgument("clienteId") { type = NavType.IntType; defaultValue = -1 })
            ) { back ->
                val clienteId = back.arguments?.getInt("clienteId")?.takeIf { it != -1 }
                ClienteFormScreen(
                    clienteId = clienteId,
                    onBack    = { mainNavController.popBackStack() },
                    onSaved   = { mainNavController.popBackStack() }
                )
            }

            // ── Veículos ───────────────────────────────────────────────────────
            composable(Routes.Veiculos.route) {
                VeiculoListScreen(
                    onVeiculoClick = { id -> mainNavController.navigate(Routes.VeiculoDetail.createRoute(id)) },
                    onAddClick     = { mainNavController.navigate(Routes.VeiculoForm.createRoute()) }
                )
            }
            composable(
                route = Routes.VeiculoDetail.route,
                arguments = listOf(navArgument("veiculoId") { type = NavType.IntType })
            ) { back ->
                val veiculoId = back.arguments!!.getInt("veiculoId")
                VeiculoDetailScreen(
                    veiculoId         = veiculoId,
                    onBack            = { mainNavController.popBackStack() },
                    onEdit            = { mainNavController.navigate(Routes.VeiculoForm.createRoute(veiculoId)) },
                    onOSClick         = { osId -> mainNavController.navigate(Routes.OSDetail.createRoute(osId)) },
                    onOrcamentoClick  = { orcId -> mainNavController.navigate(Routes.OrcamentoDetail.createRoute(orcId)) }
                )
            }
            composable(
                route = Routes.VeiculoForm.route,
                arguments = listOf(
                    navArgument("veiculoId") { type = NavType.IntType; defaultValue = -1 },
                    navArgument("clienteId") { type = NavType.IntType; defaultValue = -1 }
                )
            ) { back ->
                val veiculoId = back.arguments?.getInt("veiculoId")?.takeIf { it != -1 }
                val clienteId = back.arguments?.getInt("clienteId")?.takeIf { it != -1 }
                VeiculoFormScreen(
                    veiculoId = veiculoId,
                    clienteId = clienteId,
                    onBack    = { mainNavController.popBackStack() },
                    onSaved   = { mainNavController.popBackStack() }
                )
            }

            // ── OS ─────────────────────────────────────────────────────────────
            composable(Routes.Ordens.route) {
                OSListScreen(
                    onOSClick  = { osId -> mainNavController.navigate(Routes.OSDetail.createRoute(osId)) },
                    onAddClick = { mainNavController.navigate(Routes.OSForm.createRoute()) }
                )
            }
            composable(
                route = Routes.OSDetail.route,
                arguments = listOf(navArgument("osId") { type = NavType.IntType })
            ) { back ->
                val osId = back.arguments!!.getInt("osId")
                OSDetailScreen(
                    onBack         = { mainNavController.popBackStack() },
                    onEdit         = { id -> mainNavController.navigate(Routes.OSForm.createRoute(id)) },
                    onLaudo        = { id -> mainNavController.navigate(Routes.Laudo.createRoute(id)) },
                    onFotos        = { id -> mainNavController.navigate(Routes.FotoGallery.createRoute(id)) },
                    onNewOrcamento = { osId -> mainNavController.navigate(Routes.OrcamentoForm.createRoute(osId = osId)) },
                    onOrcamentos   = { mainNavController.navigate(Routes.Orcamentos.route) }

                )
            }
            composable(
                route = Routes.Laudo.route,
                arguments = listOf(navArgument("osId") { type = NavType.IntType })
            ) {
                LaudoScreen(
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(
                route = Routes.FotoGallery.route,
                arguments = listOf(navArgument("osId") { type = NavType.IntType })
            ) {
                FotoGalleryScreen(
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(
                route = Routes.OSForm.route,
                arguments = listOf(
                    navArgument("osId")      { type = NavType.IntType; defaultValue = -1 },
                    navArgument("veiculoId") { type = NavType.IntType; defaultValue = -1 }
                )
            ) { back ->
                val osId      = back.arguments?.getInt("osId")?.takeIf { it != -1 }
                val veiculoId = back.arguments?.getInt("veiculoId")?.takeIf { it != -1 }
                OSFormScreen(
                    osId      = osId,
                    veiculoId = veiculoId,
                    onBack    = { mainNavController.popBackStack() },
                    onSaved   = { mainNavController.popBackStack() }
                )
            }
            composable(Routes.EntradaRapida.route) {
                EntradaRapidaScreen(
                    onBack     = { mainNavController.popBackStack() },
                    onOSCriada = { osId ->
                        mainNavController.navigate(Routes.OSDetail.createRoute(osId)) {
                            popUpTo(Routes.Dashboard.route)
                        }
                    }
                )
            }

            // ── Orçamentos ─────────────────────────────────────────────────────
            composable(Routes.Orcamentos.route) {
                OrcamentoListScreen(
                    onOrcamentoClick = { id -> mainNavController.navigate(Routes.OrcamentoDetail.createRoute(id)) },
                    onAddClick       = { mainNavController.navigate(Routes.OrcamentoForm.createRoute()) }
                )
            }
            composable(
                route = Routes.OrcamentoDetail.route,
                arguments = listOf(navArgument("orcamentoId") { type = NavType.IntType })
            ) {
                OrcamentoDetailScreen(
                    onBack = { mainNavController.popBackStack() },
                    onEdit = { id -> mainNavController.navigate(Routes.OrcamentoForm.createRoute(id)) }
                )
            }
            composable(
                route = Routes.OrcamentoForm.route,
                arguments = listOf(
                    navArgument("orcamentoId") { type = NavType.IntType; defaultValue = -1 },
                    navArgument("osId")        { type = NavType.IntType; defaultValue = -1 }
                )
            ) { back ->
                val orcamentoId = back.arguments?.getInt("orcamentoId")?.takeIf { it != -1 }
                val osId        = back.arguments?.getInt("osId")?.takeIf { it != -1 }
                OrcamentoFormScreen(
                    orcamentoId = orcamentoId,
                    osId        = osId,
                    onBack      = { mainNavController.popBackStack() },
                    onSaved     = { mainNavController.popBackStack() }
                )
            }

            // ── Menu ───────────────────────────────────────────────────────────
            composable(Routes.Menu.route) {
                MenuScreen(
                    onLogout = {
                        rootNavController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Main.route) { inclusive = true }
                        }
                    },
                    onTrocarOficina = {
                        rootNavController.navigate(Routes.OficinaSelecao.route) {
                            popUpTo(Routes.Main.route) { inclusive = true }
                        }
                    },
                    onTemplates = { mainNavController.navigate(Routes.TemplateList.route) },
                    onOficina   = { mainNavController.navigate(Routes.Oficina.route) },
                    onUsuarios  = { mainNavController.navigate(Routes.Usuarios.route) }
                )
            }

            // ── Templates ──────────────────────────────────────────────────────
            composable(Routes.TemplateList.route) {
                TemplateListScreen(
                    onBack     = { mainNavController.popBackStack() },
                    onAddClick = { mainNavController.navigate(Routes.TemplateForm.createRoute()) },
                    onEditClick = { id -> mainNavController.navigate(Routes.TemplateForm.createRoute(id)) }
                )
            }
            composable(
                route = Routes.TemplateForm.route,
                arguments = listOf(navArgument("templateId") { type = NavType.IntType; defaultValue = -1 })
            ) {
                TemplateFormScreen(
                    onBack  = { mainNavController.popBackStack() },
                    onSaved = { mainNavController.popBackStack() }
                )
            }

            // ── Busca global ───────────────────────────────────────────────────
            composable(Routes.Search.route) {
                SearchScreen(
                    onBack           = { mainNavController.popBackStack() },
                    onClienteClick   = { id -> mainNavController.navigate(Routes.ClienteDetail.createRoute(id)) },
                    onVeiculoClick   = { id -> mainNavController.navigate(Routes.VeiculoDetail.createRoute(id)) },
                    onOrcamentoClick = { id -> mainNavController.navigate(Routes.OrcamentoDetail.createRoute(id)) },
                    onOSClick        = { id -> mainNavController.navigate(Routes.OSDetail.createRoute(id)) }
                )
            }

            // ── Perfil da Oficina ──────────────────────────────────────────────
            composable(Routes.Oficina.route) {
                OficinaPerfilScreen(
                    onBack = { mainNavController.popBackStack() }
                )
            }

            // ── Usuários ───────────────────────────────────────────────────────
            composable(Routes.Usuarios.route) {
                UsuariosScreen(
                    onBack     = { mainNavController.popBackStack() },
                    onAddClick = { mainNavController.navigate(Routes.UsuarioForm.createRoute()) },
                    onEditClick = { id -> mainNavController.navigate(Routes.UsuarioForm.createRoute(id)) }
                )
            }
            composable(
                route = Routes.UsuarioForm.route,
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType; defaultValue = -1 })
            ) {
                UsuarioFormScreen(
                    onBack  = { mainNavController.popBackStack() },
                    onSaved = { mainNavController.popBackStack() }
                )
            }
        } // NavHost
        } // Column
    }
}
