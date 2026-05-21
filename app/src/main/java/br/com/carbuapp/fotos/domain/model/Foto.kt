package br.com.carbuapp.fotos.domain.model

data class Foto(
    val id: Int,
    val osId: Int,
    val url: String,       // caminho relativo: "fotos/filename.jpg"
    val descricao: String?,
    val zona: String?,
    val criadoEm: String?
)
