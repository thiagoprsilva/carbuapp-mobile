package br.com.carbuapp.search.data

import br.com.carbuapp.search.domain.SearchResult
import br.com.carbuapp.search.domain.SearchResultType

data class SearchResponseDto(
    val q: String,
    val results: List<SearchResultDto>
)

data class SearchResultDto(
    val type: String,
    val id: Int,
    val title: String,
    val subtitle: String?,
    val href: String?
)

fun SearchResultDto.toDomain() = SearchResult(
    type = when (type) {
        "CLIENTE"    -> SearchResultType.CLIENTE
        "VEICULO"    -> SearchResultType.VEICULO
        "ORCAMENTO"  -> SearchResultType.ORCAMENTO
        "REGISTRO"   -> SearchResultType.REGISTRO
        else         -> SearchResultType.REGISTRO
    },
    id       = id,
    title    = title,
    subtitle = subtitle ?: "",
    href     = href ?: ""
)
