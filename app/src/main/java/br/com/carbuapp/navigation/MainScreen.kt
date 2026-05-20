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
import br.com.carbuapp.veiculos.ui.VeiculoDetailScreen
import br.com.carbuapp.veiculos.ui.VeiculoFormScreen
import br.com.carbuapp.veiculos.ui.VeiculoListScreen

@Composable
fun MainScreen(rootNavController: NavHostController) {
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
                DashboardScreen()
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
            composable(Routes.Ordens.route) { PlaceholderScreen("Ordens de Serviço") }
            composable(
                route = Routes.OSDetail.route,
                arguments = listOf(navArgument("osId") { type = NavType.IntType })
            ) { PlaceholderScreen("Detalhe OS") }

            // ── Orçamentos ─────────────────────────────────────────────────────
            composable(Routes.Orcamentos.route) { PlaceholderScreen("Orçamentos") }
            composable(
                route = Routes.OrcamentoDetail.route,
                arguments = listOf(navArgument("orcamentoId") { type = NavType.IntType })
            ) { PlaceholderScreen("Detalhe Orçamento") }

            // ── Menu ───────────────────────────────────────────────────────────
            composable(Routes.Menu.route) { PlaceholderScreen("Menu") }
        }
    }
}
