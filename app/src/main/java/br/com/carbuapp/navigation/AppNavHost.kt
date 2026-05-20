package br.com.carbuapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.carbuapp.auth.ui.LoginScreen
import br.com.carbuapp.oficina.ui.OficinaSelecaoScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        // ── Login ──────────────────────────────────────────────────────────────
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = { isSuperAdmin ->
                    if (isSuperAdmin) {
                        // Superadmin deve escolher a oficina antes de entrar
                        navController.navigate(Routes.OficinaSelecao.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.Main.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // ── Seleção de oficina (superadmin) ────────────────────────────────────
        composable(Routes.OficinaSelecao.route) {
            OficinaSelecaoScreen(
                onOficinaSelected = {
                    navController.navigate(Routes.Main.route) {
                        popUpTo(Routes.OficinaSelecao.route) { inclusive = true }
                    }
                }
            )
        }

        // ── App principal ──────────────────────────────────────────────────────
        composable(Routes.Main.route) {
            MainScreen(rootNavController = navController)
        }
    }
}
