package br.com.carbuapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import br.com.carbuapp.menu.ui.MenuScreen
import br.com.carbuapp.orcamentos.ui.OrcamentoDetailScreen
import br.com.carbuapp.orcamentos.ui.OrcamentoFormScreen
import br.com.carbuapp.orcamentos.ui.OrcamentoListScreen
import br.com.carbuapp.ordens.ui.OSDetailScreen
import br.com.carbuapp.ordens.ui.OSFormScreen
import br.com.carbuapp.ordens.ui.OSListScreen
import br.com.carbuapp.veiculos.ui.VeiculoDetailScreen
import br.com.carbuapp.veiculos.ui.VeiculoFormScreen
import br.com.carbuapp.veiculos.ui.VeiculoListScreen

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = mainNavController) }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = Routes.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ── Dashboard ──────────────────────────────────────────────────────
            composable(Routes.Dashboard.route) {
                DashboardScreen(
                    onOSClick        = { osId  -> mainNavController.navigate(Routes.OSDetail.createRoute(osId)) },
                    onOrcamentoClick = { orcId -> mainNavController.navigate(Routes.OrcamentoDetail.createRoute(orcId)) }
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
                    onBack = { mainNavController.popBackStack() },
                    onEdit = { id -> mainNavController.navigate(Routes.OSForm.createRoute(id)) }
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
                    }
                )
            }
        }
    }
}
