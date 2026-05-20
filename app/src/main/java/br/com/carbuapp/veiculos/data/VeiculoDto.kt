package br.com.carbuapp.veiculos.data

import br.com.carbuapp.veiculos.data.local.VeiculoEntity
import br.com.carbuapp.veiculos.domain.model.TimelineEvento
import br.com.carbuapp.veiculos.domain.model.Veiculo
import com.google.gson.annotations.SerializedName

data class ClienteResumoDto(
    val id: Int,
    val nome: String,
    val telefone: String?
)

data class VeiculoDto(
    val id: Int,
    val placa: String,
    val modelo: String,
    val ano: String?,
    val motor: String?,
    val alimentacao: String?,
    val clienteId: Int,
    val createdAt: String,
    val cliente: ClienteResumoDto?
)

data class VeiculoRequest(
    val placa: String,
    val modelo: String,
    val ano: String?,
    val motor: String?,
    val alimentacao: String?,
    val clienteId: Int
)

// Timeline DTOs
data class TimelineRegistroDto(
    val tipo: String,       // "registro"
    val id: Int,
    val data: String,
    val categoria: String,
    val descricao: String,
    val observacoes: String?
)

data class TimelineOrcamentoDto(
    val tipo: String,       // "orcamento"
    val id: Int,
    val data: String,
    val numero: Int,
    val total: Double
)

// Mappers
fun VeiculoDto.toDomain() = Veiculo(
    id = id,
    placa = placa,
    modelo = modelo,
    ano = ano,
    motor = motor,
    alimentacao = alimentacao,
    clienteId = clienteId,
    clienteNome = cliente?.nome,
    createdAt = createdAt
)

fun VeiculoDto.toEntity() = VeiculoEntity(
    id = id,
    placa = placa,
    modelo = modelo,
    ano = ano,
    motor = motor,
    alimentacao = alimentacao,
    clienteId = clienteId,
    clienteNome = cliente?.nome,
    createdAt = createdAt
)

fun VeiculoEntity.toDomain() = Veiculo(
    id = id,
    placa = placa,
    modelo = modelo,
    ano = ano,
    motor = motor,
    alimentacao = alimentacao,
    clienteId = clienteId,
    clienteNome = clienteNome,
    createdAt = createdAt
)

fun Veiculo.toEntity() = VeiculoEntity(
    id = id,
    placa = placa,
    modelo = modelo,
    ano = ano,
    motor = motor,
    alimentacao = alimentacao,
    clienteId = clienteId,
    clienteNome = clienteNome,
    createdAt = createdAt
)
