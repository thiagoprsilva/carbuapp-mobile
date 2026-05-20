package br.com.carbuapp.orcamentos.data

import br.com.carbuapp.orcamentos.data.local.OrcamentoEntity
import br.com.carbuapp.orcamentos.domain.model.Orcamento
import br.com.carbuapp.orcamentos.domain.model.OrcamentoDetalhe
import br.com.carbuapp.orcamentos.domain.model.OrcamentoItem

// ── Sub-DTOs ──────────────────────────────────────────────────────────────────

data class OrcamentoItemDto(
    val id: Int,
    val descricao: String,
    val qtd: Double,
    val precoUnit: Double,
    val valorLinha: Double
)

data class ClienteResumoOrcDto(
    val id: Int,
    val nome: String,
    val telefone: String?
)

data class VeiculoResumoOrcDto(
    val id: Int,
    val placa: String,
    val modelo: String,
    val cliente: ClienteResumoOrcDto?
)

data class OSResumoOrcDto(
    val id: Int,
    val numero: Int,
    val status: String
)

// ── Principal ─────────────────────────────────────────────────────────────────

data class OrcamentoDto(
    val id: Int,
    val numero: Int,
    val status: String,
    val subtotal: Double,
    val total: Double,
    val createdAt: String,
    val veiculoId: Int,
    val veiculo: VeiculoResumoOrcDto?,
    val registroTecnicoId: Int,
    val registroTecnico: OSResumoOrcDto?,
    val itens: List<OrcamentoItemDto>
)

// ── Requests ──────────────────────────────────────────────────────────────────

data class OrcamentoItemRequest(
    val descricao: String,
    val qtd: Double,
    val precoUnit: Double
)

data class OrcamentoCreateRequest(
    val registroTecnicoId: Int,
    val itens: List<OrcamentoItemRequest>
)

data class OrcamentoUpdateRequest(
    val itens: List<OrcamentoItemRequest>
)

data class OrcamentoStatusRequest(
    val status: String
)

// ── Mappers ───────────────────────────────────────────────────────────────────

fun OrcamentoDto.toDomain() = Orcamento(
    id = id,
    numero = numero,
    status = status,
    subtotal = subtotal,
    total = total,
    createdAt = createdAt,
    veiculoId = veiculoId,
    placa = veiculo?.placa ?: "",
    modelo = veiculo?.modelo ?: "",
    clienteNome = veiculo?.cliente?.nome ?: "",
    clienteId = veiculo?.cliente?.id ?: 0,
    osId = registroTecnicoId,
    osNumero = registroTecnico?.numero ?: 0
)

fun OrcamentoDto.toEntity() = OrcamentoEntity(
    id = id,
    numero = numero,
    status = status,
    subtotal = subtotal,
    total = total,
    createdAt = createdAt,
    veiculoId = veiculoId,
    placa = veiculo?.placa ?: "",
    modelo = veiculo?.modelo ?: "",
    clienteNome = veiculo?.cliente?.nome ?: "",
    clienteId = veiculo?.cliente?.id ?: 0,
    osId = registroTecnicoId,
    osNumero = registroTecnico?.numero ?: 0
)

fun OrcamentoEntity.toDomain() = Orcamento(
    id = id,
    numero = numero,
    status = status,
    subtotal = subtotal,
    total = total,
    createdAt = createdAt,
    veiculoId = veiculoId,
    placa = placa,
    modelo = modelo,
    clienteNome = clienteNome,
    clienteId = clienteId,
    osId = osId,
    osNumero = osNumero
)

fun OrcamentoItemDto.toDomain() = OrcamentoItem(
    id = id,
    descricao = descricao,
    qtd = qtd,
    precoUnit = precoUnit,
    valorLinha = valorLinha
)

fun OrcamentoDto.toDetalhe() = OrcamentoDetalhe(
    orcamento = toDomain(),
    itens = itens.map { it.toDomain() }
)
