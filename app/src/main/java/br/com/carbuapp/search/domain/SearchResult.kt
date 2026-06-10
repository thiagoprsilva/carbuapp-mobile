package br.com.carbuapp.search.domain

enum class SearchResultType { CLIENTE, VEICULO, ORCAMENTO, REGISTRO }

data class SearchResult(
    val type: SearchResultType,
    val id: Int,
    val title: String,
    val subtitle: String,
    val href: String
)
