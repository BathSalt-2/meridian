package ai.or4cl3.meridian.ui.dashboard

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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    onNavigateToIris: () -> Unit,
    onNavigateToLocus: () -> Unit,
    onNavigateToPraxis: (alertId: String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MeridianTopBar(
                communityName = state.communityName,
                engineStatus = state.engineStatus,
                unreadAlerts = state.unreadAlertCount
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToIris,
                icon = { Icon(Icons.Filled.CameraAlt, contentDescription = null) },
                text = { Text("Observe") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Engine status banner
            if (state.engineStatus != EngineStatus.READY) {
                item { EngineStatusBanner(state.engineStatus) }
            }

            // Active PRAXIS alerts
            if (state.activeAlerts.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Active Alerts",
                        count = state.activeAlerts.size,
                        icon = Icons.Filled.Warning
                    )
                }
                items(state.activeAlerts.take(3), key = { it.id }) { alert ->
                    PraxisAlertCard(
                        alert = alert,
                        onClick = {
                            viewModel.markAlertRead(alert.id)
                            onNavigateToPraxis(alert.id)
                        }
                    )
                }
            }

            // Quick actions
            item {
                SectionHeader(title = "Quick Actions", icon = Icons.Filled.Bolt)
                QuickActionsRow(
                    onObserve = onNavigateToIris,
                    onViewMap = onNavigateToLocus,
                    onViewAlerts = { onNavigateToPraxis("none") }
                )
            }

            // Recent observations
            if (state.recentObservations.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Recent Observations",
                        count = state.recentObservations.size,
                        icon = Icons.Filled.Visibility
                    )
                }
                items(state.recentObservations.take(5), key = { it.id }) { obs ->
                    ObservationSummaryCard(obs)
                }
            }

            // Community stats
            item {
                CommunityStatsCard(
                    placeCount = state.registeredPlaces.size,
                    observationCount = state.recentObservations.size
                )
            }

            item { Spacer(Modifier.height(80.dp)) } // FAB clearance
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeridianTopBar(
    communityName: String,
    engineStatus: EngineStatus,
    unreadAlerts: Int
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = if (communityName.isNotBlank()) communityName else "MERIDIAN",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ecological Intelligence",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        actions = {
            // Engine readiness indicator
            Box(
                modifier = Modifier.padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (engineStatus) {
                        EngineStatus.READY -> Icons.Filled.Psychology
                        EngineStatus.LOADING -> Icons.Filled.HourglassEmpty
                        else -> Icons.Filled.Warning
                    },
                    contentDescription = "Engine status",
                    tint = when (engineStatus) {
                        EngineStatus.READY -> MeridianGreen
                        EngineStatus.LOADING -> MeridianAmber
                        else -> MeridianRed
                    }
                )
            }
            if (unreadAlerts > 0) {
                BadgedBox(badge = { Badge { Text("$unreadAlerts") } }) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Alerts")
                }
                Spacer(Modifier.width(12.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun EngineStatusBanner(status: EngineStatus) {
    val message = when (status) {
        EngineStatus.LOADING -> "Gemma 4 is loading… IRIS and PRAXIS will be ready shortly."
        EngineStatus.DEGRADED -> "Running in low-memory mode (E2B). Some features reduced."
        EngineStatus.UNAVAILABLE -> "AI engine unavailable. Check model installation."
        EngineStatus.READY -> ""
    }
    if (message.isNotBlank()) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Info, contentDescription = null)
                Text(message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun PraxisAlertCard(alert: PraxisAlert, onClick: () -> Unit) {
    val severityColor = when (alert.severity) {
        AlertSeverity.INFO -> SeverityInfo
        AlertSeverity.WARNING -> SeverityWarning
        AlertSeverity.URGENT -> SeverityUrgent
        AlertSeverity.CRITICAL -> SeverityCritical
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, severityColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                SeverityChip(alert.severity, severityColor)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = alert.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                maxLines = 3
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = alert.alertType.name.replace('_', ' '),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatRelativeTime(alert.generatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun SeverityChip(severity: AlertSeverity, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = severity.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickActionsRow(
    onObserve: () -> Unit,
    onViewMap: () -> Unit,
    onViewAlerts: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(Icons.Filled.CameraAlt, "Observe", Modifier.weight(1f), onObserve)
        QuickActionCard(Icons.Filled.Map, "LOCUS Map", Modifier.weight(1f), onViewMap)
        QuickActionCard(Icons.Filled.Analytics, "PRAXIS", Modifier.weight(1f), onViewAlerts)
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ObservationSummaryCard(observation: Observation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (observation.category) {
                    ObservationCategory.CROP_HEALTH -> Icons.Filled.Eco
                    ObservationCategory.SOIL -> Icons.Filled.Landscape
                    ObservationCategory.WATER_QUALITY -> Icons.Filled.Water
                    ObservationCategory.ACOUSTIC -> Icons.Filled.RecordVoiceOver
                    else -> Icons.Filled.Visibility
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = observation.classification,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${(observation.confidence * 100).toInt()}% confidence • ${formatRelativeTime(observation.timestamp)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            observation.severityStage?.let { stage ->
                SeverityDot(stage)
            }
        }
    }
}

@Composable
private fun SeverityDot(stage: Int) {
    val color = when (stage) {
        1, 2 -> MeridianGreen
        3 -> MeridianAmber
        4 -> SeverityUrgent
        else -> SeverityCritical
    }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = color
    ) {
        Text(
            text = "$stage",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.surface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CommunityStatsCard(placeCount: Int, observationCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MeridianGreenContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("$placeCount", "Registered Places")
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
            )
            StatItem("$observationCount", "Total Observations")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Text(
            text = if (count != null) "$title ($count)" else title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}
