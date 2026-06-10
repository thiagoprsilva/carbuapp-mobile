package br.com.carbuapp.usuarios

import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.usuarios.domain.Usuario
import br.com.carbuapp.usuarios.domain.UsuarioRepository
import br.com.carbuapp.usuarios.ui.UsuariosViewModel
import br.com.carbuapp.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuariosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: UsuarioRepository
    private lateinit var viewModel: UsuariosViewModel

    private val fakeUsuarios = listOf(
        Usuario(1, "Ana Costa",    "ana@oficina.com",    "ADMIN",    true,  1),
        Usuario(2, "Carlos Lima",  "carlos@oficina.com", "MECANICO", true,  1),
        Usuario(3, "Bia Souza",    "bia@oficina.com",    "MECANICO", false, 1)
    )

    @Before
    fun setUp() {
        repository = mockk()
    }

    // ── Carregamento ──────────────────────────────────────────────────────────

    @Test
    fun `init carrega usuários com sucesso`() = runTest {
        coEvery { repository.listar() } returns Result.success(fakeUsuarios)

        viewModel = UsuariosViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(3, (state as UiState.Success).data.size)
    }

    @Test
    fun `lista vazia retorna estado Empty`() = runTest {
        coEvery { repository.listar() } returns Result.success(emptyList())

        viewModel = UsuariosViewModel(repository)
        advanceUntilIdle()

        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `erro na listagem retorna estado Error`() = runTest {
        coEvery { repository.listar() } returns Result.failure(Exception("Sem conexão"))

        viewModel = UsuariosViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).message.contains("Sem conexão"))
    }

    @Test
    fun `load recarrega lista ao ser chamado novamente`() = runTest {
        coEvery { repository.listar() } returns Result.success(fakeUsuarios)

        viewModel = UsuariosViewModel(repository)
        advanceUntilIdle()

        viewModel.load()
        advanceUntilIdle()

        coVerify(exactly = 2) { repository.listar() }
    }

    // ── Reset de senha ────────────────────────────────────────────────────────

    @Test
    fun `resetarSenha com sucesso chama callback e limpa actionState`() = runTest {
        coEvery { repository.listar() } returns Result.success(fakeUsuarios)
        coEvery { repository.resetarSenha(1, "novaSenha123") } returns Result.success(Unit)

        viewModel = UsuariosViewModel(repository)
        advanceUntilIdle()

        var callbackInvoked = false
        viewModel.resetarSenha(1, "novaSenha123") { callbackInvoked = true }
        advanceUntilIdle()

        assertTrue(callbackInvoked)
        assertTrue(viewModel.actionState.value is UiState.Success)
        coVerify(exactly = 1) { repository.resetarSenha(1, "novaSenha123") }
    }

    @Test
    fun `resetarSenha com falha emite estado Error`() = runTest {
        coEvery { repository.listar() } returns Result.success(fakeUsuarios)
        coEvery { repository.resetarSenha(any(), any()) } returns
                Result.failure(Exception("Usuário não encontrado"))

        viewModel = UsuariosViewModel(repository)
        advanceUntilIdle()

        viewModel.resetarSenha(99, "qualquerSenha") {}
        advanceUntilIdle()

        val state = viewModel.actionState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).message.contains("não encontrado"))
    }

    @Test
    fun `resetActionState limpa o estado de ação`() = runTest {
        coEvery { repository.listar() } returns Result.success(fakeUsuarios)
        coEvery { repository.resetarSenha(any(), any()) } returns
                Result.failure(Exception("Erro"))

        viewModel = UsuariosViewModel(repository)
        advanceUntilIdle()

        viewModel.resetarSenha(1, "teste") {}
        advanceUntilIdle()

        assertTrue(viewModel.actionState.value is UiState.Error)
        viewModel.resetActionState()
        assertEquals(UiState.Idle, viewModel.actionState.value)
    }

    // ── Filtros de usuário ────────────────────────────────────────────────────

    @Test
    fun `lista contém usuários ativos e inativos`() = runTest {
        coEvery { repository.listar() } returns Result.success(fakeUsuarios)

        viewModel = UsuariosViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Success
        val ativos   = state.data.filter { it.ativo }
        val inativos = state.data.filter { !it.ativo }
        assertEquals(2, ativos.size)
        assertEquals(1, inativos.size)
    }
}
