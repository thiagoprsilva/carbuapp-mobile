package br.com.carbuapp.fotos.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.carbuapp.core.util.UiState
import br.com.carbuapp.fotos.domain.model.Foto
import coil.compose.AsyncImage
import java.io.File

private const val BASE_URL = "https://api.carbuapp.com.br/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoGalleryScreen(
    onBack: () -> Unit,
    viewModel: FotoGalleryViewModel = hiltViewModel()
) {
    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val context     = LocalContext.current

    val snackbarHost = remember { SnackbarHostState() }
    var showSourceDialog  by remember { mutableStateOf(false) }
    var fotoParaExcluir   by remember { mutableStateOf<Foto?>(null) }

    // URI temporário para captura pela câmera
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(actionState) {
        when (val s = actionState) {
            is UiState.Error -> {
                snackbarHost.showSnackbar(s.message)
                viewModel.resetActionState()
            }
            else -> Unit
        }
    }

    // ── Launcher: galeria ────────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.upload(it) }
    }

    // ── Launcher: câmera ─────────────────────────────────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { saved ->
        if (saved) cameraUri?.let { viewModel.upload(it) }
    }

    // ── Launcher: permissão câmera ────────────────────────────────────────────
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createCameraUri(context)
            cameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // ── Launcher: permissão galeria (Android 13+) ─────────────────────────────
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) galleryLauncher.launch("image/*")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotos da OS") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSourceDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar foto")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            if (actionState is UiState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }

            when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is UiState.Empty -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Nenhuma foto registrada.\nToque em + para adicionar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }

                is UiState.Error -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load) { Text("Tentar novamente") }
                }

                is UiState.Success -> FotoGrid(
                    fotos = state.data,
                    onDeleteClick = { fotoParaExcluir = it }
                )

                else -> Unit
            }
        }
    }

    // ── Diálogo: escolher fonte da foto ──────────────────────────────────────
    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Adicionar foto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ListItem(
                        headlineContent = { Text("Câmera") },
                        leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showSourceDialog = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Galeria") },
                        leadingContent = { Icon(Icons.Default.Photo, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showSourceDialog = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                galleryLauncher.launch("image/*")
                            }
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSourceDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // ── Diálogo: confirmar exclusão ──────────────────────────────────────────
    fotoParaExcluir?.let { foto ->
        AlertDialog(
            onDismissRequest = { fotoParaExcluir = null },
            title = { Text("Excluir foto") },
            text  = { Text("Tem certeza que deseja excluir esta foto? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        fotoParaExcluir = null
                        viewModel.delete(foto.id)
                    }
                ) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { fotoParaExcluir = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FotoGrid(fotos: List<Foto>, onDeleteClick: (Foto) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(fotos, key = { it.id }) { foto ->
            FotoCard(foto = foto, onDeleteClick = { onDeleteClick(foto) })
        }
    }
}

@Composable
private fun FotoCard(foto: Foto, onDeleteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
    ) {
        AsyncImage(
            model = "$BASE_URL${foto.url}",
            contentDescription = foto.descricao ?: "Foto da OS",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Botão de excluir no canto superior direito
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDeleteClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Excluir foto",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/** Cria um arquivo temporário de câmera e retorna seu URI via FileProvider. */
private fun createCameraUri(context: Context): Uri {
    val cameraDir = File(context.cacheDir, "camera").also { it.mkdirs() }
    val file = File(cameraDir, "foto_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
