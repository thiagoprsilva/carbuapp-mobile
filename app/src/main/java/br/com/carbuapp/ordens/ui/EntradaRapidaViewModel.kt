package br.com.carbuapp.ordens.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.clientes.domain.ClienteRepository
import br.com.carbuapp.clientes.domain.model.Cliente
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.ordens.domain.AvariaCreateRequest
import br.com.carbuapp.ordens.domain.LaudoCreateRequest
import br.com.carbuapp.ordens.domain.OSCreateRequest
import br.com.carbuapp.ordens.domain.OSRepository
import br.com.carbuapp.veiculos.domain.VeiculoCreateRequest
import br.com.carbuapp.veiculos.domain.VeiculoRepository
import br.com.carbuapp.veiculos.domain.model.Veiculo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class AvariaFormState(
    val zona: String = "",
    val severidade: String? = null,
    val observacao: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EntradaRapidaViewModel @Inject constructor(
    private val clienteRepo: ClienteRepository,
    private val veiculoRepo: VeiculoRepository,
    private val osRepo: OSRepository
) : ViewModel() {

    // ── Step ─────────────────────────────────────────────────────────────────
    val step = MutableStateFlow(1)

    // ── Step 1: Cliente ───────────────────────────────────────────────────────
    val clienteSearch = MutableStateFlow("")
    private val _todosClientes = MutableStateFlow<List<Cliente>>(emptyList())

    val clientesFiltrados: StateFlow<List<Cliente>> =
        combine(clienteSearch, _todosClientes) { query, lista ->
            if (query.isBlank()) lista
            else lista.filter {
                it.nome.contains(query, ignoreCase = true) ||
                it.telefone?.contains(query) == true
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedCliente = MutableStateFlow<Cliente?>(null)
    val showNovoClienteForm = MutableStateFlow(false)
    val novoClienteNome = MutableStateFlow("")
    val novoClienteTelefone = MutableStateFlow("")

    // ── Step 1: Veículo ───────────────────────────────────────────────────────
    val veiculosDoCliente: StateFlow<List<Veiculo>> = selectedCliente
        .flatMapLatest { cliente ->
            if (cliente != null) veiculoRepo.observeByCliente(cliente.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedVeiculo = MutableStateFlow<Veiculo?>(null)
    val showNovoVeiculoForm = MutableStateFlow(false)
    val novoVeiculoPlaca = MutableStateFlow("")
    val novoVeiculoModelo = MutableStateFlow("")
    val novoVeiculoAno = MutableStateFlow("")

    // ── Step 2: Laudo ─────────────────────────────────────────────────────────
    val laudoAtivo = MutableStateFlow(false)
    val laudoKm = MutableStateFlow("")
    val laudoNivelCombust = MutableStateFlow("")
    val laudoObservacoes = MutableStateFlow("")
    val avarias = MutableStateFlow<List<AvariaFormState>>(emptyList())

    // ── Step 3: OS ────────────────────────────────────────────────────────────
    val categoria = MutableStateFlow("")
    val dataServico = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    val descricao = MutableStateFlow("")
    val osObservacoes = MutableStateFlow("")

    // ── Action state ──────────────────────────────────────────────────────────
    private val _actionState = MutableStateFlow<UiState<Int>>(UiState.Idle)
    val actionState: StateFlow<UiState<Int>> = _actionState.asStateFlow()

    init {
        viewModelScope.launch {
            clienteRepo.observeAll().collect { _todosClientes.value = it }
        }
        viewModelScope.launch { clienteRepo.refresh() }
    }

    // ── Cliente ───────────────────────────────────────────────────────────────
    fun selectCliente(cliente: Cliente) {
        selectedCliente.value = cliente
        selectedVeiculo.value = null
        showNovoClienteForm.value = false
        clienteSearch.value = cliente.nome
        viewModelScope.launch { veiculoRepo.refresh(clienteId = cliente.id) }
    }

    fun criarCliente() {
        val nome = novoClienteNome.value.trim()
        if (nome.isBlank()) {
            _actionState.value = UiState.Error("Informe o nome do cliente")
            return
        }
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            clienteRepo.create(nome, novoClienteTelefone.value.trim().ifBlank { null })
                .onSuccess { novo ->
                    selectCliente(novo)
                    novoClienteNome.value = ""
                    novoClienteTelefone.value = ""
                    _actionState.value = UiState.Idle
                }
                .onFailure { _actionState.value = UiState.Error(it.message ?: "Erro ao criar cliente") }
        }
    }

    // ── Veículo ───────────────────────────────────────────────────────────────
    fun selectVeiculo(veiculo: Veiculo) {
        selectedVeiculo.value = veiculo
        showNovoVeiculoForm.value = false
    }

    fun criarVeiculo() {
        val placa = novoVeiculoPlaca.value.trim().uppercase()
        val modelo = novoVeiculoModelo.value.trim()
        val clienteId = selectedCliente.value?.id ?: return
        if (placa.isBlank() || modelo.isBlank()) {
            _actionState.value = UiState.Error("Preencha placa e modelo")
            return
        }
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            veiculoRepo.create(
                VeiculoCreateRequest(
                    placa = placa,
                    modelo = modelo,
                    ano = novoVeiculoAno.value.trim().ifBlank { null },
                    motor = null,
                    alimentacao = null,
                    clienteId = clienteId
                )
            ).onSuccess { novo ->
                selectVeiculo(novo)
                novoVeiculoPlaca.value = ""
                novoVeiculoModelo.value = ""
                novoVeiculoAno.value = ""
                _actionState.value = UiState.Idle
            }.onFailure { _actionState.value = UiState.Error(it.message ?: "Erro ao criar veículo") }
        }
    }

    // ── Navegação de steps ────────────────────────────────────────────────────
    fun nextStep() {
        if (step.value == 1 && selectedVeiculo.value == null) {
            _actionState.value = UiState.Error("Selecione um veículo para continuar")
            return
        }
        if (step.value < 3) step.value++
    }

    fun prevStep() {
        if (step.value > 1) step.value--
    }

    // ── Avarias ───────────────────────────────────────────────────────────────
    fun addAvaria() {
        avarias.value = avarias.value + AvariaFormState()
    }

    fun removeAvaria(index: Int) {
        avarias.value = avarias.value.toMutableList().apply { removeAt(index) }
    }

    fun updateAvaria(index: Int, avaria: AvariaFormState) {
        avarias.value = avarias.value.toMutableList().apply { set(index, avaria) }
    }

    // ── Criar OS ──────────────────────────────────────────────────────────────
    fun criarOS() {
        val veiculoId = selectedVeiculo.value?.id ?: run {
            _actionState.value = UiState.Error("Selecione um veículo")
            return
        }
        if (categoria.value.isBlank()) {
            _actionState.value = UiState.Error("Selecione a categoria")
            return
        }
        if (descricao.value.isBlank()) {
            _actionState.value = UiState.Error("Preencha a descrição")
            return
        }

        val laudoRequest = if (laudoAtivo.value) {
            LaudoCreateRequest(
                km = laudoKm.value.trim().toIntOrNull(),
                nivelCombust = laudoNivelCombust.value.ifBlank { null },
                observacoes = laudoObservacoes.value.trim().ifBlank { null },
                avarias = avarias.value
                    .filter { it.zona.isNotBlank() }
                    .map { AvariaCreateRequest(it.zona, it.severidade, it.observacao.ifBlank { null }) }
            )
        } else null

        viewModelScope.launch {
            _actionState.value = UiState.Loading
            osRepo.create(
                OSCreateRequest(
                    veiculoId = veiculoId,
                    categoria = categoria.value,
                    descricao = descricao.value.trim(),
                    dataServico = dataServico.value,
                    observacoes = osObservacoes.value.trim().ifBlank { null },
                    laudo = laudoRequest
                )
            ).onSuccess { os -> _actionState.value = UiState.Success(os.id) }
             .onFailure { _actionState.value = UiState.Error(it.message ?: "Erro ao criar OS") }
        }
    }

    fun resetActionState() {
        _actionState.value = UiState.Idle
    }
}
