package ai.or4cl3.meridian.ui.onboarding

import ai.or4cl3.meridian.data.preferences.CommunityPreferences
import ai.or4cl3.meridian.model.CommunityProfile
import ai.or4cl3.meridian.model.ModelTier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 0,
    val communityName: String = "",
    val primaryLanguage: String = "en",
    val region: String = "",
    val isSaving: Boolean = false,
    val isComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val communityPreferences: CommunityPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateCommunityName(name: String) = _uiState.update { it.copy(communityName = name) }
    fun updateLanguage(lang: String) = _uiState.update { it.copy(primaryLanguage = lang) }
    fun updateRegion(region: String) = _uiState.update { it.copy(region = region) }

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (current < 2) {
            _uiState.update { it.copy(currentStep = current + 1) }
        } else {
            completeOnboarding()
        }
    }

    fun prevStep() {
        val current = _uiState.value.currentStep
        if (current > 0) _uiState.update { it.copy(currentStep = current - 1) }
    }

    private fun completeOnboarding() {
        val state = _uiState.value
        if (state.communityName.isBlank()) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val profile = CommunityProfile(
                communityId = UUID.randomUUID().toString(),
                communityName = state.communityName,
                primaryLanguage = state.primaryLanguage,
                region = state.region,
                deviceId = UUID.randomUUID().toString(),
                isPrimaryDevice = true,
                onboardingComplete = true,
                onboardingStep = 3,
                preferredModel = ModelTier.E4B
            )
            communityPreferences.saveCommunityProfile(profile)
            _uiState.update { it.copy(isSaving = false, isComplete = true) }
        }
    }
}
