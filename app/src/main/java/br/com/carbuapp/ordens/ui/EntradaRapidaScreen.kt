package br.com.carbuapp.ordens.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.clientes.domain.model.Cliente
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.laudos.domain.model.NIVEIS_COMBUSTIVEL
import br.com.carbuapp.laudos.domain.model.ZONAS_VEICULO
import br.com.carbuapp.veiculos.domain.model.Veiculo
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradaRapidaScreen(
    onBack: () -> Unit,
    onOSCriada: (osId: Int) -> Unit,
    viewModel: EntradaRapidaViewModel = hiltViewModel()
) {
    val step by viewModel.step.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    BackHandler(enabled = step > 1) { viewModel.prevStep() }

    LaunchedEffect(actionState) {
        when (val s = actionState) {
            is UiState.Error -> {
                snackbarHost.showSnackbar(s.message)
                viewModel.resetActionState()
            }
            is UiState.Success -> onOSCriada(s.data)
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entrada de Veículo") },
                navigationIcon = {
                    IconButton(onClick = { if (step > 1) viewModel.prevStep() else onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Indicador de step ────────────────────────────────────────────
            WizardStepIndicator(currentStep = step)

            // ── Conteúdo do step ─────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (step) {
                    1 -> Step1ClienteVeiculo(viewModel = viewModel)
                    2 -> Step2Laudo(viewModel = viewModel)
                    3 -> Step3OSForm(viewModel = viewModel)
                }
            }

            // ── Botões de navegação ──────────────────────────────────────────
            val isLoading = actionState is UiState.Loading
            Surface(tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = viewModel::prevStep,
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) { Text("Voltar") }
                    }
                    Button(
                        onClick = { if (step < 3) viewModel.nextStep() else viewModel.criarOS() },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(if (step < 3) "Próximo" else "Criar OS")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WizardStepIndicator(currentStep: Int) {
    val labels = listOf("Cliente / Veículo", "Laudo", "Ordem de Serviço")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        labels.forEachIndexed { idx, label ->
            val step = idx + 1
            val isActive = step == currentStep
            val isDone = step < currentStep
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when {
                        isDone   -> MaterialTheme.colorScheme.primary
                        isActive -> MaterialTheme.colorScheme.primaryContainer
                        else     -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                ) {}
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Step 1: Cliente + Veículo ─────────────────────────────────────────────────

@Composable
private fun Step1ClienteVeiculo(viewModel: EntradaRapidaViewModel) {
    val clienteSearch by viewModel.clienteSearch.collectAsStateWithLifecycle()
    val clientes by viewModel.clientesFiltrados.collectAsStateWithLifecycle()
    val selectedCliente by viewModel.selectedCliente.collectAsStateWithLifecycle()
    val showNovoClienteForm by viewModel.showNovoClienteForm.collectAsStateWithLifecycle()
    val novoClienteNome by viewModel.novoClienteNome.collectAsStateWithLifecycle()
    val novoClienteTelefone by viewModel.novoClienteTelefone.collectAsStateWithLifecycle()

    val veiculos by viewModel.veiculosDoCliente.collectAsStateWithLifecycle()
    val selectedVeiculo by viewModel.selectedVeiculo.collectAsStateWithLifecycle()
    val showNovoVeiculoForm by viewModel.showNovoVeiculoForm.collectAsStateWithLifecycle()
    val novoVeiculoPlaca by viewModel.novoVeiculoPlaca.collectAsStateWithLifecycle()
    val novoVeiculoModelo by viewModel.novoVeiculoModelo.collectAsStateWithLifecycle()
    val novoVeiculoAno by viewModel.novoVeiculoAno.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Seção Cliente ────────────────────────────────────────────────────
        Text("1. Cliente", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

        if (selectedCliente != null) {
            ClienteSelecionadoCard(
                cliente = selectedCliente!!,
                onRemover = {
                    viewModel.selectedCliente.value = null
                    viewModel.selectedVeiculo.value = null
                    viewModel.clienteSearch.value = ""
                }
            )
        } else {
            OutlinedTextField(
                value = clienteSearch,
                onValueChange = { viewModel.clienteSearch.value = it },
                label = { Text("Buscar cliente") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (clientes.isNotEmpty()) {
                Card {
                    Column {
                        clientes.take(5).forEachIndexed { idx, cliente ->
                            if (idx > 0) HorizontalDivider()
                            ListItem(
                                headlineContent = { Text(cliente.nome) },
                                supportingContent = cliente.telefone?.let { { Text(it) } },
                                leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier.clickable { viewModel.selectCliente(cliente) }
                            )
                        }
                    }
                }
            }

            if (!showNovoClienteForm) {
                OutlinedButton(
                    onClick = { viewModel.showNovoClienteForm.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Novo cliente")
                }
            } else {
                NovoClienteForm(
                    nome = novoClienteNome,
                    telefone = novoClienteTelefone,
                    onNomeChange = { viewModel.novoClienteNome.value = it },
                    onTelefoneChange = { viewModel.novoClienteTelefone.value = it },
                    onCancelar = { viewModel.showNovoClienteForm.value = false },
                    onCriar = viewModel::criarCliente
                )
            }
        }

        // ── Seção Veículo ────────────────────────────────────────────────────
        if (selectedCliente != null) {
            HorizontalDivider()
            Text("2. Veículo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

            if (selectedVeiculo != null) {
                VeiculoSelecionadoCard(
                    veiculo = selectedVeiculo!!,
                    onRemover = { viewModel.selectedVeiculo.value = null }
                )
            } else {
                if (veiculos.isNotEmpty()) {
                    Card {
                        Column {
                            veiculos.forEachIndexed { idx, veiculo ->
                                if (idx > 0) HorizontalDivider()
                                ListItem(
                                    headlineContent = { Text("${veiculo.placa} · ${veiculo.modelo}") },
                                    supportingContent = veiculo.ano?.let { { Text("Ano: $it") } },
                                    leadingContent = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                                    modifier = Modifier.clickable { viewModel.selectVeiculo(veiculo) }
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Nenhum veículo para este cliente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!showNovoVeiculoForm) {
                    OutlinedButton(
                        onClick = { viewModel.showNovoVeiculoForm.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Novo veículo")
                    }
                } else {
                    NovoVeiculoForm(
                        placa = novoVeiculoPlaca,
                        modelo = novoVeiculoModelo,
                        ano = novoVeiculoAno,
                        onPlacaChange = { viewModel.novoVeiculoPlaca.value = it.uppercase() },
                        onModeloChange = { viewModel.novoVeiculoModelo.value = it },
                        onAnoChange = { viewModel.novoVeiculoAno.value = it },
                        onCancelar = { viewModel.showNovoVeiculoForm.value = false },
                        onCriar = viewModel::criarVeiculo
                    )
                }
            }
        }
    }
}

@Composable
private fun ClienteSelecionadoCard(cliente: Cliente, onRemover: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Column {
                    Text(cliente.nome, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    cliente.telefone?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) }
                }
            }
            IconButton(onClick = onRemover) {
                Icon(Icons.Default.Close, contentDescription = "Remover cliente", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun VeiculoSelecionadoCard(veiculo: Veiculo, onRemover: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Column {
                    Text("${veiculo.placa} · ${veiculo.modelo}", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    veiculo.ano?.let { Text("Ano: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) }
                }
            }
            IconButton(onClick = onRemover) {
                Icon(Icons.Default.Close, contentDescription = "Remover veículo", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun NovoClienteForm(
    nome: String,
    telefone: String,
    onNomeChange: (String) -> Unit,
    onTelefoneChange: (String) -> Unit,
    onCancelar: () -> Unit,
    onCriar: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Novo cliente", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = nome,
                onValueChange = onNomeChange,
                label = { Text("Nome *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = telefone,
                onValueChange = onTelefoneChange,
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                Button(onClick = onCriar, modifier = Modifier.weight(1f)) { Text("Criar") }
            }
        }
    }
}

@Composable
private fun NovoVeiculoForm(
    placa: String,
    modelo: String,
    ano: String,
    onPlacaChange: (String) -> Unit,
    onModeloChange: (String) -> Unit,
    onAnoChange: (String) -> Unit,
    onCancelar: () -> Unit,
    onCriar: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Novo veículo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = placa,
                onValueChange = onPlacaChange,
                label = { Text("Placa *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = modelo,
                onValueChange = onModeloChange,
                label = { Text("Modelo *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = ano,
                onValueChange = onAnoChange,
                label = { Text("Ano") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                Button(onClick = onCriar, modifier = Modifier.weight(1f)) { Text("Criar") }
            }
        }
    }
}

// ── Step 2: Laudo ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step2Laudo(viewModel: EntradaRapidaViewModel) {
    val laudoAtivo by viewModel.laudoAtivo.collectAsStateWithLifecycle()
    val laudoKm by viewModel.laudoKm.collectAsStateWithLifecycle()
    val laudoNivelCombust by viewModel.laudoNivelCombust.collectAsStateWithLifecycle()
    val laudoObservacoes by viewModel.laudoObservacoes.collectAsStateWithLifecycle()
    val avarias by viewModel.avarias.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle
        Card {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Preencher laudo de entrada", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text("Registre o estado do veículo ao recebê-lo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = laudoAtivo, onCheckedChange = { viewModel.laudoAtivo.value = it })
            }
        }

        if (laudoAtivo) {
            OutlinedTextField(
                value = laudoKm,
                onValueChange = { viewModel.laudoKm.value = it },
                label = { Text("Quilometragem") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Nível de combustível
            var nivelExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = nivelExpanded,
                onExpandedChange = { nivelExpanded = !nivelExpanded }
            ) {
                OutlinedTextField(
                    value = laudoNivelCombust,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nível de combustível") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nivelExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = nivelExpanded,
                    onDismissRequest = { nivelExpanded = false }
                ) {
                    NIVEIS_COMBUSTIVEL.forEach { nivel ->
                        DropdownMenuItem(
                            text = { Text(nivel) },
                            onClick = { viewModel.laudoNivelCombust.value = nivel; nivelExpanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = laudoObservacoes,
                onValueChange = { viewModel.laudoObservacoes.value = it },
                label = { Text("Observações") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Avarias", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FilledTonalButton(onClick = viewModel::addAvaria) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Adicionar")
                }
            }

            if (avarias.isEmpty()) {
                Text(
                    "Nenhuma avaria registrada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            avarias.forEachIndexed { idx, avaria ->
                AvariaFormCard(
                    avaria = avaria,
                    onUpdate = { viewModel.updateAvaria(idx, it) },
                    onRemove = { viewModel.removeAvaria(idx) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvariaFormCard(
    avaria: AvariaFormState,
    onUpdate: (AvariaFormState) -> Unit,
    onRemove: () -> Unit
) {
    var zonaExpanded by remember { mutableStateOf(false) }

    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Avaria", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remover avaria", modifier = Modifier.size(18.dp))
                }
            }

            ExposedDropdownMenuBox(
                expanded = zonaExpanded,
                onExpandedChange = { zonaExpanded = !zonaExpanded }
            ) {
                OutlinedTextField(
                    value = avaria.zona,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Zona *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = zonaExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = zonaExpanded,
                    onDismissRequest = { zonaExpanded = false }
                ) {
                    ZONAS_VEICULO.forEach { zona ->
                        DropdownMenuItem(
                            text = { Text(zona) },
                            onClick = { onUpdate(avaria.copy(zona = zona)); zonaExpanded = false }
                        )
                    }
                }
            }

            // Severidade chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Triple("leve", "Leve", MaterialTheme.colorScheme.tertiaryContainer),
                       Triple("moderado", "Moderado", MaterialTheme.colorScheme.secondaryContainer),
                       Triple("grave", "Grave", MaterialTheme.colorScheme.errorContainer))
                    .forEach { (value, label, bgColor) ->
                        val selected = avaria.severidade == value
                        val fgColor = when (value) {
                            "leve"     -> MaterialTheme.colorScheme.onTertiaryContainer
                            "moderado" -> MaterialTheme.colorScheme.onSecondaryContainer
                            else       -> MaterialTheme.colorScheme.onErrorContainer
                        }
                        FilterChip(
                            selected = selected,
                            onClick = { onUpdate(avaria.copy(severidade = if (selected) null else value)) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = bgColor,
                                selectedLabelColor = fgColor
                            )
                        )
                    }
            }

            OutlinedTextField(
                value = avaria.observacao,
                onValueChange = { onUpdate(avaria.copy(observacao = it)) },
                label = { Text("Observação") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

// ── Step 3: Formulário OS ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step3OSForm(viewModel: EntradaRapidaViewModel) {
    val categoria by viewModel.categoria.collectAsStateWithLifecycle()
    val dataServico by viewModel.dataServico.collectAsStateWithLifecycle()
    val descricao by viewModel.descricao.collectAsStateWithLifecycle()
    val osObservacoes by viewModel.osObservacoes.collectAsStateWithLifecycle()
    val selectedCliente by viewModel.selectedCliente.collectAsStateWithLifecycle()
    val selectedVeiculo by viewModel.selectedVeiculo.collectAsStateWithLifecycle()
    val laudoAtivo by viewModel.laudoAtivo.collectAsStateWithLifecycle()

    var categoriaExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val categorias = listOf(
        "Revisão", "Elétrica", "Funilaria", "Mecânica",
        "Suspensão", "Freios", "Ar-condicionado", "Outro"
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = runCatching {
                LocalDate.parse(dataServico, DateTimeFormatter.ISO_LOCAL_DATE)
                    .atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
            }.getOrNull()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        viewModel.dataServico.value = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Resumo ───────────────────────────────────────────────────────────
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Resumo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Text("Cliente: ${selectedCliente?.nome ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Veículo: ${selectedVeiculo?.let { "${it.placa} · ${it.modelo}" } ?: "—"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("Laudo: ${if (laudoAtivo) "Sim" else "Não"}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // ── Categoria ────────────────────────────────────────────────────────
        ExposedDropdownMenuBox(
            expanded = categoriaExpanded,
            onExpandedChange = { categoriaExpanded = !categoriaExpanded }
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoriaExpanded,
                onDismissRequest = { categoriaExpanded = false }
            ) {
                categorias.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = { viewModel.categoria.value = cat; categoriaExpanded = false }
                    )
                }
            }
        }

        // ── Data do serviço ──────────────────────────────────────────────────
        OutlinedTextField(
            value = dataServico,
            onValueChange = {},
            readOnly = true,
            label = { Text("Data do serviço *") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Calendário")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // ── Descrição ────────────────────────────────────────────────────────
        OutlinedTextField(
            value = descricao,
            onValueChange = { viewModel.descricao.value = it },
            label = { Text("Descrição *") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        // ── Observações ──────────────────────────────────────────────────────
        OutlinedTextField(
            value = osObservacoes,
            onValueChange = { viewModel.osObservacoes.value = it },
            label = { Text("Observações") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
