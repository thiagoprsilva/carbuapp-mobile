package br.com.carbuapp.search

import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.search.domain.SearchRepository
import br.com.carbuapp.search.domain.SearchResult
import br.com.carbuapp.search.domain.SearchResultType
import br.com.carbuapp.search.ui.SearchViewModel
import br.com.carbuapp.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: SearchRepository
    private lateinit var viewModel: SearchViewModel

    private val fakeResults = listOf(
        SearchResult(SearchResultType.CLIENTE,   1, "João Silva",      "cliente@email.com", "/clientes/1"),
        SearchResult(SearchResultType.VEICULO,   2, "Toyota Corolla",  "ABC-1234",          "/veiculos/2"),
        SearchResult(SearchResultType.REGISTRO,  3, "OS #42",          "Toyota Corolla",    "/registros/3")
    )

    @Before
    fun setUp() {
        repository = mockk()
        viewModel  = SearchViewModel(repository)
    }

    @Test
    fun `estado inicial é Idle`() {
        assertEquals(UiState.Idle, viewModel.uiState.value)
        assertEquals("", viewModel.query.value)
    }

    @Test
    fun `query menor que 2 chars mantém estado Idle`() = runTest {
        viewModel.onQueryChange("a")
        advanceUntilIdle()

        assertEquals(UiState.Idle, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.search(any()) }
    }

    @Test
    fun `query válida dispara busca após debounce`() = runTest {
        coEvery { repository.search("João") } returns fakeResults

        viewModel.onQueryChange("João")
        advanceTimeBy(500)           // avança além dos 400 ms de debounce
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(3, (state as UiState.Success).data.size)
        coVerify(exactly = 1) { repository.search("João") }
    }

    @Test
    fun `busca sem resultados retorna estado Empty`() = runTest {
        coEvery { repository.search(any()) } returns emptyList()

        viewModel.onQueryChange("xyzwnotfound")
        advanceTimeBy(500)
        advanceUntilIdle()

        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `erro na busca retorna estado Error com mensagem`() = runTest {
        coEvery { repository.search(any()) } throws RuntimeException("Sem conexão")

        viewModel.onQueryChange("teste")
        advanceTimeBy(500)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).message.contains("Sem conexão"))
    }

    @Test
    fun `clearQuery limpa query e volta para Idle`() = runTest {
        coEvery { repository.search(any()) } returns fakeResults

        viewModel.onQueryChange("João")
        advanceTimeBy(500)
        advanceUntilIdle()

        viewModel.clearQuery()

        assertEquals("", viewModel.query.value)
        assertEquals(UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `resultados são agrupados por tipo corretamente`() = runTest {
        coEvery { repository.search(any()) } returns fakeResults

        viewModel.onQueryChange("query")
        advanceTimeBy(500)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Success
        val clientes = state.data.filter { it.type == SearchResultType.CLIENTE }
        val veiculos = state.data.filter { it.type == SearchResultType.VEICULO }
        assertEquals(1, clientes.size)
        assertEquals(1, veiculos.size)
    }
}
