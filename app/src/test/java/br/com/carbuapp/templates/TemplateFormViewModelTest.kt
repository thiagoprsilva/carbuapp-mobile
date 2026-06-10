package br.com.carbuapp.templates

import androidx.lifecycle.SavedStateHandle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.templates.domain.model.Template
import br.com.carbuapp.templates.domain.model.TemplateItem
import br.com.carbuapp.templates.domain.usecase.GetTemplatesUseCase
import br.com.carbuapp.templates.domain.usecase.SaveTemplateUseCase
import br.com.carbuapp.templates.ui.TemplateFormViewModel
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
class TemplateFormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getTemplates: GetTemplatesUseCase
    private lateinit var saveTemplate: SaveTemplateUseCase

    private fun buildViewModel(templateId: Int? = null): TemplateFormViewModel {
        val handle = SavedStateHandle(mapOf("templateId" to (templateId ?: -1)))
        return TemplateFormViewModel(handle, getTemplates, saveTemplate)
    }

    @Before
    fun setUp() {
        getTemplates = mockk()
        saveTemplate = mockk()
    }

    // ── Novo template ──────────────────────────────────────────────────────────

    @Test
    fun `novo template começa com estado Success e um item vazio`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is UiState.Success)
        assertEquals(1, vm.itens.value.size)
        assertEquals("", vm.nome.value)
        assertNull(vm.templateId)
    }

    @Test
    fun `save com nome em branco emite erro sem chamar API`() = runTest {
        val vm = buildViewModel()
        vm.onNomeChange("   ")
        vm.updateDescricao(0, "Troca de óleo")

        vm.save {}
        advanceUntilIdle()

        val state = vm.actionState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).message.lowercase().contains("nome"))
        coVerify(exactly = 0) { saveTemplate(any()) }
    }

    @Test
    fun `save sem itens com descrição emite erro`() = runTest {
        val vm = buildViewModel()
        vm.onNomeChange("Revisão Completa")
        // item com descrição em branco (padrão)

        vm.save {}
        advanceUntilIdle()

        val state = vm.actionState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).message.lowercase().contains("item"))
        coVerify(exactly = 0) { saveTemplate(any()) }
    }

    @Test
    fun `save válido chama saveTemplate e invoca callback`() = runTest {
        val templateRetorno = Template(id = 1, nome = "Revisão Completa", createdAt = null,
            itens = listOf(TemplateItem(1, "Troca de óleo", 1, 150.0)))
        coEvery { saveTemplate(any()) } returns templateRetorno

        val vm = buildViewModel()
        vm.onNomeChange("Revisão Completa")
        vm.updateDescricao(0, "Troca de óleo")
        vm.updateQtd(0, "1")
        vm.updatePreco(0, "150.00")

        var callbackInvoked = false
        vm.save { callbackInvoked = true }
        advanceUntilIdle()

        assertTrue(vm.actionState.value is UiState.Success)
        assertTrue(callbackInvoked)
        coVerify(exactly = 1) { saveTemplate(any()) }
    }

    @Test
    fun `addItem aumenta lista de itens`() = runTest {
        val vm = buildViewModel()
        assertEquals(1, vm.itens.value.size)

        vm.addItem()
        assertEquals(2, vm.itens.value.size)

        vm.addItem()
        assertEquals(3, vm.itens.value.size)
    }

    @Test
    fun `removeItem remove o item pelo índice correto`() = runTest {
        val vm = buildViewModel()
        vm.addItem()
        vm.updateDescricao(0, "Item A")
        vm.updateDescricao(1, "Item B")

        vm.removeItem(0)

        assertEquals(1, vm.itens.value.size)
        assertEquals("Item B", vm.itens.value[0].descricao)
    }

    @Test
    fun `updateQtd aceita apenas dígitos`() = runTest {
        val vm = buildViewModel()
        vm.updateQtd(0, "2abc")

        assertEquals("2", vm.itens.value[0].qtd)
    }

    // ── Total estimado ─────────────────────────────────────────────────────────

    @Test
    fun `total estimado calculado corretamente para múltiplos itens`() = runTest {
        val vm = buildViewModel()
        vm.updateDescricao(0, "Óleo")
        vm.updateQtd(0, "2")
        vm.updatePreco(0, "50.00")

        vm.addItem()
        vm.updateDescricao(1, "Filtro")
        vm.updateQtd(1, "1")
        vm.updatePreco(1, "30.00")

        val total = vm.itens.value.sumOf {
            (it.qtd.toIntOrNull() ?: 1) * (it.precoUnit.toDoubleOrNull() ?: 0.0)
        }
        assertEquals(130.0, total, 0.01)
    }

    // ── Modo edição ───────────────────────────────────────────────────────────

    @Test
    fun `modo edição carrega dados do template existente`() = runTest {
        val templateExistente = Template(
            id        = 5,
            nome      = "Revisão 10k",
            createdAt = null,
            itens = listOf(
                TemplateItem(1, "Troca de óleo", 1, 80.0),
                TemplateItem(2, "Filtro de ar",  1, 45.0)
            )
        )
        coEvery { getTemplates() } returns listOf(templateExistente)

        val vm = buildViewModel(templateId = 5)
        advanceUntilIdle()

        assertTrue(vm.uiState.value is UiState.Success)
        assertEquals("Revisão 10k", vm.nome.value)
        assertEquals(2, vm.itens.value.size)
        assertEquals("Troca de óleo", vm.itens.value[0].descricao)
    }

    @Test
    fun `resetActionState volta estado de ação para Idle`() = runTest {
        val vm = buildViewModel()
        vm.onNomeChange("   ")
        vm.save {}
        advanceUntilIdle()

        assertTrue(vm.actionState.value is UiState.Error)
        vm.resetActionState()
        assertEquals(UiState.Idle, vm.actionState.value)
    }
}
