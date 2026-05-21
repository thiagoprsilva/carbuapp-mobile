package br.com.carbuapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("Dashboard",   Routes.Dashboard.route,   Icons.Default.Dashboard),
    BottomNavItem("Clientes",    Routes.Clientes.route,    Icons.Default.Person),
    BottomNavItem("Veículos",    Routes.Veiculos.route,    Icons.Default.DirectionsCar),
    BottomNavItem("OS",          Routes.Ordens.route,      Icons.Default.Build),
    BottomNavItem("Orçamentos",  Routes.Orcamentos.route,  Icons.Default.Receipt),
    BottomNavItem("Menu",        Routes.Menu.route,        Icons.Default.Menu)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Volta para o início do gráfico evitando pilha crescente
                            popUpTo(Routes.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) }
            )
        }
    }
}
