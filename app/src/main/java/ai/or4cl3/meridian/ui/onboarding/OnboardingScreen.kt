package ai.or4cl3.meridian.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.or4cl3.meridian.ui.theme.*

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) onOnboardingComplete()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            // Progress indicator
            Spacer(Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = { (state.currentStep + 1) / 3f },
                modifier = Modifier.fillMaxWidth(),
                color = MeridianGreen
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Step ${state.currentStep + 1} of 3",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(32.dp))

            // Step content
            AnimatedContent(
                targetState = state.currentStep,
                label = "onboarding_step",
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { step ->
                when (step) {
                    0 -> WelcomeStep()
                    1 -> CommunityNameStep(
                        communityName = state.communityName,
                        region = state.region,
                        onNameChange = viewModel::updateCommunityName,
                        onRegionChange = viewModel::updateRegion
                    )
                    2 -> LanguageStep(
                        selectedLanguage = state.primaryLanguage,
                        onLanguageChange = viewModel::updateLanguage
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.currentStep > 0) {
                    OutlinedButton(onClick = viewModel::prevStep, modifier = Modifier.weight(1f)) {
                        Text("Back")
                    }
                }
                Button(
                    onClick = viewModel::nextStep,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSaving && (state.currentStep != 1 || state.communityName.isNotBlank())
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(if (state.currentStep == 2) "Start Using MERIDIAN" else "Continue")
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Filled.Eco,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MeridianGreen
        )
        Text(
            "Welcome to MERIDIAN",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Your community's ecological intelligence system.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(16.dp))
        FeatureRow(Icons.Filled.CameraAlt, "IRIS", "Analyzes your crops, soil, and water 100% on-device")
        FeatureRow(Icons.Filled.Map, "LOCUS", "Builds your community's living ecological memory")
        FeatureRow(Icons.Filled.Psychology, "PRAXIS", "Reasons proactively about threats and opportunities")
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MeridianGreenContainer)) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Lock, null, tint = MeridianGreen, modifier = Modifier.size(20.dp))
                Text(
                    "100% on-device. No data leaves your device without your permission.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, null, tint = MeridianGreen, modifier = Modifier.size(24.dp))
        Column {
            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun CommunityNameStep(
    communityName: String,
    region: String,
    onNameChange: (String) -> Unit,
    onRegionChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text("Name Your Community", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "This name will appear on your dashboard and in any data you choose to share with neighboring communities.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        OutlinedTextField(
            value = communityName,
            onValueChange = onNameChange,
            label = { Text("Community Name") },
            placeholder = { Text("e.g. Mwangi Village") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.People, null) }
        )
        OutlinedTextField(
            value = region,
            onValueChange = onRegionChange,
            label = { Text("Region (optional)") },
            placeholder = { Text("e.g. Kirinyaga County, Kenya") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Public, null) }
        )
    }
}

@Composable
private fun LanguageStep(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val languages = listOf(
        "en" to "English", "sw" to "Swahili", "hi" to "Hindi",
        "bn" to "Bengali", "am" to "Amharic", "ha" to "Hausa",
        "yo" to "Yoruba", "es" to "Spanish", "fr" to "French",
        "pt" to "Portuguese", "ar" to "Arabic", "qu" to "Quechua"
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Choose Language", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "MERIDIAN supports 140+ languages. Select your community's primary language for voice and text interaction.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        languages.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (code, name) ->
                    FilterChip(
                        selected = selectedLanguage == code,
                        onClick = { onLanguageChange(code) },
                        label = { Text(name, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Pad row if needed
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
