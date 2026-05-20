package br.com.carbuapp.dashboard.data

import br.com.carbuapp.dashboard.domain.*

data class TotaisDto(
    val clientes: Int,
    val veiculos: Int,
    val registros: Int,
    val orcamentos: Int
)

data class VeiculoResumoDto(
    val id: Int,
    val placa: String,
    val modelo: String,
    val cliente: ClienteResumoDto
)

data class ClienteResumoDto(val id: Int, val nome: String)

data class OSRecenteDto(
    val id: Int,
    val numero: Int,
    val status: String,
    val categoria: String,
    val descricao: String,
    val dataServico: String,
    val veiculo: VeiculoResumoDto
)

data class OrcamentoRecenteDto(
    val id: Int,
    val numero: Int,
    val total: Double,
    val createdAt: String,
    val veiculo: VeiculoResumoDto
)

data class RecentesDto(
    val registros: List<OSRecenteDto>,
    val orcamentos: List<OrcamentoRecenteDto>
)

data class DashboardSummaryDto(
    val totais: TotaisDto,
    val recentes: RecentesDto
)

fun DashboardSummaryDto.toDomain() = DashboardSummary(
    totais = Totais(
        clientes   = totais.clientes,
        veiculos   = totais.veiculos,
        registros  = totais.registros,
        orcamentos = totais.orcamentos
    ),
    recentes = Recentes(
        registros = recentes.registros.map {
            OSRecente(
                id          = it.id,
                numero      = it.numero,
                status      = it.status,
                categoria   = it.categoria,
                descricao   = it.descricao,
                dataServico = it.dataServico.take(10),
                placa       = it.veiculo.placa,
                modelo      = it.veiculo.modelo,
                clienteNome = it.veiculo.cliente.nome
            )
        },
        orcamentos = recentes.orcamentos.map {
            OrcamentoRecente(
                id          = it.id,
                numero      = it.numero,
                total       = it.total,
                createdAt   = it.createdAt.take(10),
                placa       = it.veiculo.placa,
                modelo      = it.veiculo.modelo,
                clienteNome = it.veiculo.cliente.nome
            )
        }
    )
)
