package ai.or4cl3.meridian.ui.dashboard

import ai.or4cl3.meridian.ai.GemmaEngine
import ai.or4cl3.meridian.data.preferences.CommunityPreferences
import ai.or4cl3.meridian.data.repository.LocusRepository
import ai.or4cl3.meridian.model.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val communityName: String = "",
    val activeAlerts: List<PraxisAlert> = emptyList(),
    val unreadAlertCount: Int = 0,
    val recentObservations: List<Observation> = emptyList(),
    val registeredPlaces: List<Place> = emptyList(),
    val engineStatus: EngineStatus = EngineStatus.LOADING,
    val isLoading: Boolean = true
)

enum class EngineStatus { LOADING, READY, DEGRADED, UNAVAILABLE }

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val locusRepository: LocusRepository,
    private val communityPreferences: CommunityPreferences,
    private val gemmaEngine: GemmaEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboardData()
        updateEngineStatus()
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            // Combine all dashboard data sources into a single reactive stream
            combine(
                communityPreferences.communityProfile,
                locusRepository.observeActiveAlerts(),
                locusRepository.observeUnreadAlertCount(),
                locusRepository.observeRecentObservations(20),
                locusRepository.observeAllPlaces()
            ) { profile, alerts, unreadCount, observations, places ->
                DashboardUiState(
                    communityName = profile?.communityName ?: "",
                    activeAlerts = alerts,
                    unreadAlertCount = unreadCount,
                    recentObservations = observations,
                    registeredPlaces = places,
                    engineStatus = if (gemmaEngine.isReady()) EngineStatus.READY else EngineStatus.LOADING,
                    isLoading = false
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    private fun updateEngineStatus() {
        viewModelScope.launch {
            // Poll engine readiness — model loading takes 3-10 seconds
            kotlinx.coroutines.delay(1000)
            while (!gemmaEngine.isReady()) {
                kotlinx.coroutines.delay(2000)
            }
            _uiState.update { it.copy(engineStatus = EngineStatus.READY) }
        }
    }

    fun markAlertRead(alertId: String) {
        viewModelScope.launch { locusRepository.markAlertRead(alertId) }
    }
}
