package br.com.carbuapp.navigation

sealed class Routes(val route: String) {

    // Auth
    object Login : Routes("login")

    // Shell principal (contém BottomNavBar)
    object Main : Routes("main")

    // Bottom Nav destinations
    object Dashboard  : Routes("dashboard")
    object Clientes   : Routes("clientes")
    object Veiculos   : Routes("veiculos")
    object Ordens     : Routes("ordens")
    object Orcamentos : Routes("orcamentos")
    object Menu       : Routes("menu")

    // Clientes
    object ClienteDetail : Routes("clientes/{clienteId}") {
        fun createRoute(clienteId: Int) = "clientes/$clienteId"
    }
    object ClienteForm : Routes("clientes/form?clienteId={clienteId}") {
        fun createRoute(clienteId: Int? = null) =
            if (clienteId != null) "clientes/form?clienteId=$clienteId"
            else "clientes/form"
    }

    // Veículos
    object VeiculoDetail : Routes("veiculos/{veiculoId}") {
        fun createRoute(veiculoId: Int) = "veiculos/$veiculoId"
    }
    object VeiculoForm : Routes("veiculos/form?veiculoId={veiculoId}&clienteId={clienteId}") {
        fun createRoute(veiculoId: Int? = null, clienteId: Int? = null): String {
            val params = buildList {
                if (veiculoId != null) add("veiculoId=$veiculoId")
                if (clienteId != null) add("clienteId=$clienteId")
            }
            return if (params.isEmpty()) "veiculos/form" else "veiculos/form?${params.joinToString("&")}"
        }
    }

    // Ordens de Serviço
    object OSDetail : Routes("ordens/{osId}") {
        fun createRoute(osId: Int) = "ordens/$osId"
    }
    object OSForm : Routes("ordens/form?osId={osId}") {
        fun createRoute(osId: Int? = null) =
            if (osId != null) "ordens/form?osId=$osId" else "ordens/form"
    }
    object EntradaRapida : Routes("ordens/entrada-rapida")

    // Orçamentos
    object OrcamentoDetail : Routes("orcamentos/{orcamentoId}") {
        fun createRoute(orcamentoId: Int) = "orcamentos/$orcamentoId"
    }
    object OrcamentoForm : Routes("orcamentos/form?orcamentoId={orcamentoId}") {
        fun createRoute(orcamentoId: Int? = null) =
            if (orcamentoId != null) "orcamentos/form?orcamentoId=$orcamentoId" else "orcamentos/form"
    }

    // Perfil / Configurações
    object Perfil    : Routes("perfil")
    object Oficina   : Routes("oficina")
    object Usuarios  : Routes("usuarios")
    object Templates : Routes("templates")
}
