package ai.or4cl3.meridian.ui.praxis

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.or4cl3.meridian.model.*
import ai.or4cl3.meridian.ui.theme.*

@Composable
fun PraxisScreen(
    initialAlertId: String?,
    onBack: () -> Unit,
    viewModel: PraxisViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(initialAlertId) {
        if (initialAlertId != null) viewModel.selectAlert(initialAlertId)
    }

    if (state.selectedAlert != null) {
        AlertDetailView(
            alert = state.selectedAlert!!,
            streamingReasoning = state.streamingReasoning,
            isStreaming = state.isStreaming,
            onBack = { viewModel.selectAlert(null) },
            onResolve = { viewModel.resolveAlert(state.selectedAlert!!.id) }
        )
    } else {
        AlertListView(
            alerts = state.alerts,
            isLoading = state.isLoading,
            onBack = onBack,
            onSelectAlert = { viewModel.selectAlert(it.id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlertListView(
    alerts: List<PraxisAlert>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onSelectAlert: (PraxisAlert) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PRAXIS Intelligence", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (alerts.isEmpty()) {
            EmptyAlertsState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alerts, key = { it.id }) { alert ->
                    AlertListCard(alert = alert, onClick = { onSelectAlert(alert) })
                }
            }
        }
    }
}

@Composable
private fun AlertListCard(alert: PraxisAlert, onClick: () -> Unit) {
    val severityColor = when (alert.severity) {
        AlertSeverity.INFO -> SeverityInfo
        AlertSeverity.WARNING -> SeverityWarning
        AlertSeverity.URGENT -> SeverityUrgent
        AlertSeverity.CRITICAL -> SeverityCritical
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.5.dp, severityColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = severityColor.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (alert.alertType) {
                        AlertType.THRESHOLD -> Icons.Filled.Warning
                        AlertType.PATTERN_DIVERGENCE -> Icons.Filled.Timeline
                        AlertType.REGIONAL_INTELLIGENCE -> Icons.Filled.Public
                        AlertType.OPPORTUNITY -> Icons.Filled.TrendingUp
                        AlertType.STRATEGIC -> Icons.Filled.Lightbulb
                    },
                    contentDescription = null,
                    tint = severityColor
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                if (!alert.isRead) {
                    Text(
                        "NEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(alert.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    alert.summary.take(120),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlertDetailView(
    alert: PraxisAlert,
    streamingReasoning: String,
    isStreaming: Boolean,
    onBack: () -> Unit,
    onResolve: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PRAXIS Analysis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onResolve) { Text("Resolve") }
                }
            )
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
            // Alert header
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(alert.summary, style = MaterialTheme.typography.bodyLarge)
                }
            }

            // PRAXIS Reasoning — the thinking mode output
            Text(
                "PRAXIS Reasoning Chain",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (isStreaming) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(
                    "PRAXIS is reasoning…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val reasoningText = streamingReasoning.ifBlank { alert.thinkingOutput }
            if (reasoningText.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = reasoningText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
            }

            // Action options
            if (alert.optionsJson.isNotBlank()) {
                Text(
                    "Recommended Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Card {
                    Text(
                        text = alert.optionsJson,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EmptyAlertsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MeridianGreen
            )
            Text(
                "No active alerts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "PRAXIS is monitoring your fields.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
