package ai.or4cl3.meridian.ui.iris

import android.graphics.Bitmap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import ai.or4cl3.meridian.model.ObservationCategory
import ai.or4cl3.meridian.ui.theme.*
import java.util.concurrent.Executors

@Composable
fun IrisScreen(
    onBack: () -> Unit,
    viewModel: IrisViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    when (state.mode) {
        IrisMode.IDLE, IrisMode.CAPTURING -> {
            IrisCameraView(
                selectedCategory = state.selectedCategory,
                places = state.places,
                selectedPlaceId = state.selectedPlaceId,
                onBack = onBack,
                onImageCaptured = { bitmap -> viewModel.onImageCaptured(bitmap) },
                onCategoryChange = { viewModel.setCategory(it) },
                onPlaceSelected = { viewModel.setSelectedPlace(it) }
            )
        }
        IrisMode.ANALYZING -> {
            AnalyzingOverlay()
        }
        IrisMode.RESULT -> {
            state.analysisResult?.let { result ->
                IrisResultView(
                    result = result,
                    selectedPlace = state.places.find { it.id == state.selectedPlaceId },
                    isSaved = state.isSaved,
                    onSave = { viewModel.saveObservation(deviceId = android.os.Build.ID) },
                    onReset = { viewModel.reset() },
                    onBack = onBack
                )
            }
        }
        IrisMode.ERROR -> {
            ErrorView(
                message = state.errorMessage ?: "Unknown error",
                onRetry = { viewModel.reset() },
                onBack = onBack
            )
        }
    }
}

@Composable
private fun IrisCameraView(
    selectedCategory: ObservationCategory,
    places: List<ai.or4cl3.meridian.model.Place>,
    selectedPlaceId: String?,
    onBack: () -> Unit,
    onImageCaptured: (Bitmap) -> Unit,
    onCategoryChange: (ObservationCategory) -> Unit,
    onPlaceSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imgCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()
                    imageCapture = imgCapture
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imgCapture
                        )
                    } catch (e: Exception) { /* Handle camera binding error */ }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        // Top controls overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    "IRIS Observation",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Filled.RemoveRedEye, "IRIS", tint = MeridianGreen)
            }
            Spacer(Modifier.height(8.dp))
            CategorySelector(selectedCategory, onCategoryChange)
        }

        // Capture button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            FloatingActionButton(
                onClick = {
                    val capture = imageCapture ?: return@FloatingActionButton
                    capture.takePicture(
                        executor,
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bitmap = image.toBitmap()
                                image.close()
                                onImageCaptured(bitmap)
                            }
                        }
                    )
                },
                containerColor = MeridianGreen,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = "Capture",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun CategorySelector(
    selected: ObservationCategory,
    onSelect: (ObservationCategory) -> Unit
) {
    val categories = listOf(
        ObservationCategory.CROP_HEALTH to "Crop",
        ObservationCategory.SOIL to "Soil",
        ObservationCategory.WATER_QUALITY to "Water",
        ObservationCategory.DEFORESTATION to "Forest"
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { (cat, label) ->
            FilterChip(
                selected = selected == cat,
                onClick = { onSelect(cat) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MeridianGreen,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun AnalyzingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = MeridianGreen, modifier = Modifier.size(56.dp))
            Text("IRIS is analyzing…", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "Gemma 4 E4B is running differential diagnosis",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IrisResultView(
    result: IrisAnalyzer.IrisAnalysisResult,
    selectedPlace: ai.or4cl3.meridian.model.Place?,
    isSaved: Boolean,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IRIS Analysis", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.Close, "Close") } }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) {
                    Text("New Observation")
                }
                Button(
                    onClick = onSave,
                    enabled = !isSaved && selectedPlace != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isSaved) "Saved to LOCUS" else "Save to LOCUS")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Primary finding card
            Card(
                colors = CardDefaults.cardColors(containerColor = MeridianGreenContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Primary Finding",
                            style = MaterialTheme.typography.labelSmall,
                            color = MeridianBrown
                        )
                        Text(
                            "${(result.confidence * 100).toInt()}% confidence",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MeridianGreen
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        result.primaryClassification,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    result.severityStage?.let { stage ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Severity: Stage $stage of 5",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (stage) { 1, 2 -> MeridianGreen; 3 -> MeridianAmber; else -> SeverityCritical }
                        )
                    }
                }
            }

            // Full reasoning chain
            Text("IRIS Reasoning Chain", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text(
                    text = result.evidenceChain,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Follow-up actions
            if (result.followUpActions.isNotEmpty()) {
                Text("Recommended Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                result.followUpActions.forEachIndexed { i, action ->
                    Card {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("${i + 1}.", color = MeridianGreen, fontWeight = FontWeight.Bold)
                            Text(action, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Filled.ErrorOutline, null, modifier = Modifier.size(64.dp), tint = SeverityCritical)
            Text("Analysis Error", style = MaterialTheme.typography.titleLarge)
            Text(message, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack) { Text("Go Back") }
                Button(onClick = onRetry) { Text("Retry") }
            }
        }
    }
}
