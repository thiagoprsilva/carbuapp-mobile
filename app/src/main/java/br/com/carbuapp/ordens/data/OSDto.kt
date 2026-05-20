package br.com.carbuapp.ordens.data

import br.com.carbuapp.ordens.data.local.OSEntity
import br.com.carbuapp.ordens.domain.model.OrdemServico

// ── Sub-DTOs ──────────────────────────────────────────────────────────────────

data class ClienteResumoOsDto(
    val id: Int,
    val nome: String,
    val telefone: String?
)

data class VeiculoResumoOsDto(
    val id: Int,
    val placa: String,
    val modelo: String,
    val cliente: ClienteResumoOsDto?
)

// ── OS list/create/update response ───────────────────────────────────────────

data class OSDto(
    val id: Int,
    val numero: Int,
    val status: String,
    val categoria: String,
    val descricao: String,
    val dataServico: String,
    val observacoes: String?,
    val veiculoId: Int,
    val veiculo: VeiculoResumoOsDto?
)

// ── OS detail sub-DTOs ────────────────────────────────────────────────────────

data class AvariaDto(
    val id: Int,
    val descricao: String,
    val posicao: String?
)

data class LaudoDto(
    val id: Int,
    val quilometragem: Int?,
    val nivelCombustivel: String?,
    val observacoes: String?,
    val avarias: List<AvariaDto>
)

data class FotoDto(
    val id: Int,
    val url: String,
    val descricao: String?,
    val criadoEm: String
)

data class OrcamentoItemDto(
    val id: Int,
    val descricao: String,
    val quantidade: Double,
    val valorUnitario: Double
)

data class OrcamentoResumoDto(
    val id: Int,
    val numero: Int,
    val status: String,
    val total: Double,
    val createdAt: String,
    val itens: List<OrcamentoItemDto>
)

data class OSDetalheDto(
    val id: Int,
    val numero: Int,
    val status: String,
    val categoria: String,
    val descricao: String,
    val dataServico: String,
    val observacoes: String?,
    val veiculoId: Int,
    val veiculo: VeiculoResumoOsDto?,
    val laudo: LaudoDto?,
    val fotos: List<FotoDto>,
    val orcamentos: List<OrcamentoResumoDto>
)

// ── Request body ──────────────────────────────────────────────────────────────

data class OSRequest(
    val veiculoId: Int,
    val categoria: String,
    val descricao: String,
    val dataServico: String,
    val observacoes: String?
)

data class OSStatusRequest(
    val status: String
)

// ── Mappers ───────────────────────────────────────────────────────────────────

fun OSDto.toDomain() = OrdemServico(
    id = id,
    numero = numero,
    status = status,
    categoria = categoria,
    descricao = descricao,
    dataServico = dataServico,
    observacoes = observacoes,
    veiculoId = veiculoId,
    placa = veiculo?.placa ?: "",
    modelo = veiculo?.modelo ?: "",
    clienteNome = veiculo?.cliente?.nome ?: "",
    clienteId = veiculo?.cliente?.id ?: 0
)

fun OSDto.toEntity() = OSEntity(
    id = id,
    numero = numero,
    status = status,
    categoria = categoria,
    descricao = descricao,
    dataServico = dataServico,
    observacoes = observacoes,
    veiculoId = veiculoId,
    placa = veiculo?.placa ?: "",
    modelo = veiculo?.modelo ?: "",
    clienteNome = veiculo?.cliente?.nome ?: "",
    clienteId = veiculo?.cliente?.id ?: 0
)

fun OSEntity.toDomain() = OrdemServico(
    id = id,
    numero = numero,
    status = status,
    categoria = categoria,
    descricao = descricao,
    dataServico = dataServico,
    observacoes = observacoes,
    veiculoId = veiculoId,
    placa = placa,
    modelo = modelo,
    clienteNome = clienteNome,
    clienteId = clienteId
)

fun OSDetalheDto.toDomain() = OrdemServico(
    id = id,
    numero = numero,
    status = status,
    categoria = categoria,
    descricao = descricao,
    dataServico = dataServico,
    observacoes = observacoes,
    veiculoId = veiculoId,
    placa = veiculo?.placa ?: "",
    modelo = veiculo?.modelo ?: "",
    clienteNome = veiculo?.cliente?.nome ?: "",
    clienteId = veiculo?.cliente?.id ?: 0
)

fun OSDetalheDto.toEntity() = OSEntity(
    id = id,
    numero = numero,
    status = status,
    categoria = categoria,
    descricao = descricao,
    dataServico = dataServico,
    observacoes = observacoes,
    veiculoId = veiculoId,
    placa = veiculo?.placa ?: "",
    modelo = veiculo?.modelo ?: "",
    clienteNome = veiculo?.cliente?.nome ?: "",
    clienteId = veiculo?.cliente?.id ?: 0
)
