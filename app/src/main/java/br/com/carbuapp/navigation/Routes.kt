package br.com.carbuapp.navigation

sealed class Routes(val route: String) {

    // Auth
    object Login : Routes("login")

    // Superadmin: seleção de oficina pós-login
    object OficinaSelecao : Routes("oficina-selecao")

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
    object OSForm : Routes("ordens/form?osId={osId}&veiculoId={veiculoId}") {
        fun createRoute(osId: Int? = null, veiculoId: Int? = null): String {
            val params = buildList {
                if (osId != null)      add("osId=$osId")
                if (veiculoId != null) add("veiculoId=$veiculoId")
            }
            return if (params.isEmpty()) "ordens/form" else "ordens/form?${params.joinToString("&")}"
        }
    }
    object EntradaRapida : Routes("ordens/entrada-rapida")

    // Laudo de Entrada
    object Laudo : Routes("laudos/{osId}") {
        fun createRoute(osId: Int) = "laudos/$osId"
    }

    // Fotos da OS
    object FotoGallery : Routes("fotos/{osId}") {
        fun createRoute(osId: Int) = "fotos/$osId"
    }

    // Orçamentos
    object OrcamentoDetail : Routes("orcamentos/{orcamentoId}") {
        fun createRoute(orcamentoId: Int) = "orcamentos/$orcamentoId"
    }
    object OrcamentoForm : Routes("orcamentos/form?orcamentoId={orcamentoId}&osId={osId}") {
        fun createRoute(orcamentoId: Int? = null, osId: Int? = null): String {
            val params = buildList {
                if (orcamentoId != null) add("orcamentoId=$orcamentoId")
                if (osId != null)        add("osId=$osId")
            }
            return if (params.isEmpty()) "orcamentos/form" else "orcamentos/form?${params.joinToString("&")}"
        }
    }

    // Templates de Serviço
    object TemplateList : Routes("templates")
    object TemplateForm : Routes("templates/form?templateId={templateId}") {
        fun createRoute(templateId: Int? = null) =
            if (templateId != null) "templates/form?templateId=$templateId"
            else "templates/form"
    }

    // Perfil / Configurações
    object Perfil   : Routes("perfil")
    object Oficina  : Routes("oficina")
    object Usuarios : Routes("usuarios")
}
