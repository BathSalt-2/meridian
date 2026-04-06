package ai.or4cl3.meridian.ui.praxis

import ai.or4cl3.meridian.ai.PraxisReasoner
import ai.or4cl3.meridian.data.preferences.CommunityPreferences
import ai.or4cl3.meridian.data.repository.LocusRepository
import ai.or4cl3.meridian.model.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PraxisUiState(
    val alerts: List<PraxisAlert> = emptyList(),
    val selectedAlert: PraxisAlert? = null,
    val streamingReasoning: String = "",
    val isStreaming: Boolean = false,
    val places: Map<String, Place> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class PraxisViewModel @Inject constructor(
    private val locusRepository: LocusRepository,
    private val praxisReasoner: PraxisReasoner,
    private val communityPreferences: CommunityPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PraxisUiState())
    val uiState: StateFlow<PraxisUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                locusRepository.observeActiveAlerts(),
                locusRepository.observeAllPlaces()
            ) { alerts, places ->
                _uiState.update {
                    it.copy(
                        alerts = alerts,
                        places = places.associateBy { p -> p.id },
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    fun selectAlert(alertId: String?) {
        val alert = _uiState.value.alerts.find { it.id == alertId }
        _uiState.update { it.copy(selectedAlert = alert, streamingReasoning = "") }
        if (alert != null) {
            viewModelScope.launch { locusRepository.markAlertRead(alert.id) }
            streamReasoningForAlert(alert)
        }
    }

    private fun streamReasoningForAlert(alert: PraxisAlert) {
        val place = _uiState.value.places[alert.triggeringPlaceIds] ?: return
        viewModelScope.launch {
            communityPreferences.communityProfile.first()?.let { profile ->
                val history = locusRepository.getHistoricalObservations(place.id, ObservationCategory.CROP_HEALTH)
                _uiState.update { it.copy(isStreaming = true, streamingReasoning = "") }
                praxisReasoner.generateAlertReasoning(
                    alert = alert,
                    place = place,
                    historicalObservations = history,
                    communityProfile = profile
                ).catch { e ->
                    _uiState.update { it.copy(isStreaming = false, streamingReasoning = alert.thinkingOutput) }
                }.collect { token ->
                    _uiState.update { state ->
                        state.copy(streamingReasoning = state.streamingReasoning + token)
                    }
                }
                _uiState.update { it.copy(isStreaming = false) }
            }
        }
    }

    fun resolveAlert(alertId: String) {
        viewModelScope.launch { locusRepository.resolveAlert(alertId) }
        _uiState.update { it.copy(selectedAlert = null) }
    }
}
