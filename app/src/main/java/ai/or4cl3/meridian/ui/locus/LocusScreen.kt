package ai.or4cl3.meridian.ui.locus

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.or4cl3.meridian.model.*
import ai.or4cl3.meridian.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocusScreen(
    onBack: () -> Unit,
    onOpenPraxis: (String) -> Unit,
    viewModel: LocusViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.showAddPlaceDialog) {
        AddPlaceDialog(
            onDismiss = { viewModel.showAddPlaceDialog(false) },
            onConfirm = { name, landmark, type -> viewModel.addPlace(name, landmark, type) }
        )
    }

    AnimatedContent(
        targetState = state.selectedPlace,
        label = "locus_navigation"
    ) { selectedPlace ->
        if (selectedPlace != null) {
            PlaceDetailView(
                place = selectedPlace,
                observations = state.observationsForPlace,
                alerts = viewModel.getAlertsForPlace(selectedPlace.id),
                onBack = { viewModel.clearSelectedPlace() },
                onOpenAlert = onOpenPraxis
            )
        } else {
            PlaceMapView(
                places = state.places,
                activeAlerts = state.activeAlerts,
                isLoading = state.isLoading,
                onBack = onBack,
                onSelectPlace = { viewModel.selectPlace(it) },
                onAddPlace = { viewModel.showAddPlaceDialog(true) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceMapView(
    places: List<Place>,
    activeAlerts: List<PraxisAlert>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onSelectPlace: (Place) -> Unit,
    onAddPlace: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LOCUS — Community Map", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlace) {
                Icon(Icons.Filled.AddLocation, "Add Place")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (places.isEmpty()) {
            EmptyPlacesState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Community knowledge graph summary
                    LocusSummaryCard(
                        placeCount = places.size,
                        alertCount = activeAlerts.size
                    )
                }
                items(places, key = { it.id }) { place ->
                    val placeAlerts = activeAlerts.filter { it.triggeringPlaceIds.contains(place.id) }
                    PlaceCard(
                        place = place,
                        alertCount = placeAlerts.size,
                        onClick = { onSelectPlace(place) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocusSummaryCard(placeCount: Int, alertCount: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MeridianGreenContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Map, null, tint = MeridianGreen)
                Text("$placeCount", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Places", style = MaterialTheme.typography.labelSmall)
            }
            Divider(modifier = Modifier.height(60.dp).width(1.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Warning, null, tint = if (alertCount > 0) SeverityWarning else MeridianGreen)
                Text("$alertCount", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Active Alerts", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun PlaceCard(place: Place, alertCount: Int, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (place.type) {
                    PlaceType.FIELD -> Icons.Filled.Grass
                    PlaceType.WATER_SOURCE -> Icons.Filled.Water
                    PlaceType.FOREST_PATCH -> Icons.Filled.Forest
                    PlaceType.GRAZING_AREA -> Icons.Filled.Agriculture
                    else -> Icons.Filled.Place
                },
                contentDescription = null,
                tint = MeridianGreen,
                modifier = Modifier.size(36.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    place.landmarkAnchor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    place.type.name.lowercase().replace('_', ' '),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (alertCount > 0) {
                BadgedBox(badge = { Badge { Text("$alertCount") } }) {
                    Icon(Icons.Filled.Warning, null, tint = SeverityWarning)
                }
            } else {
                Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceDetailView(
    place: Place,
    observations: List<Observation>,
    alerts: List<PraxisAlert>,
    onBack: () -> Unit,
    onOpenAlert: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(place.name, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(place.type.name.lowercase().replace('_', ' '), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(place.landmarkAnchor, style = MaterialTheme.typography.bodyLarge)
                        place.areaEstimateHa?.let {
                            Text("${it}ha", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            if (alerts.isNotEmpty()) {
                item { Text("Active Alerts (${alerts.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                items(alerts, key = { it.id }) { alert ->
                    Card(onClick = { onOpenAlert(alert.id) }) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Warning, null, tint = SeverityWarning)
                            Text(alert.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            if (observations.isNotEmpty()) {
                item { Text("Observation History (${observations.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                items(observations.take(20), key = { it.id }) { obs ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(obs.classification, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                "${(obs.confidence * 100).toInt()}% • ${obs.category.name.lowercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "No observations yet. Use IRIS to record the first observation at this location.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddPlaceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, PlaceType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PlaceType.FIELD) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Place", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Place Name") },
                    placeholder = { Text("e.g. East Maize Field") }
                )
                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Landmark Anchor") },
                    placeholder = { Text("e.g. north of the baobab tree") }
                )
                Text("Type", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlaceType.values().take(4).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name.take(5), style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank() && landmark.isNotBlank()) onConfirm(name, landmark, selectedType) },
                enabled = name.isNotBlank() && landmark.isNotBlank()
            ) { Text("Add Place") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun EmptyPlacesState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Filled.AddLocation, null, modifier = Modifier.size(64.dp), tint = MeridianGreen)
            Text("No places registered", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "Add your fields, water sources, and forest patches
to start building the LOCUS knowledge map.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
