package ai.or4cl3.meridian.ui.locus

import ai.or4cl3.meridian.data.repository.LocusRepository
import ai.or4cl3.meridian.model.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class LocusUiState(
    val places: List<Place> = emptyList(),
    val selectedPlace: Place? = null,
    val observationsForPlace: List<Observation> = emptyList(),
    val activeAlerts: List<PraxisAlert> = emptyList(),
    val isLoading: Boolean = true,
    val showAddPlaceDialog: Boolean = false
)

@HiltViewModel
class LocusViewModel @Inject constructor(
    private val locusRepository: LocusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocusUiState())
    val uiState: StateFlow<LocusUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                locusRepository.observeAllPlaces(),
                locusRepository.observeActiveAlerts()
            ) { places, alerts ->
                _uiState.update {
                    it.copy(places = places, activeAlerts = alerts, isLoading = false)
                }
            }.collect()
        }
    }

    fun selectPlace(place: Place) {
        _uiState.update { it.copy(selectedPlace = place) }
        viewModelScope.launch {
            locusRepository.observeObservationsForPlace(place.id)
                .collect { observations ->
                    _uiState.update { it.copy(observationsForPlace = observations) }
                }
        }
    }

    fun clearSelectedPlace() {
        _uiState.update { it.copy(selectedPlace = null, observationsForPlace = emptyList()) }
    }

    fun showAddPlaceDialog(show: Boolean) {
        _uiState.update { it.copy(showAddPlaceDialog = show) }
    }

    fun addPlace(name: String, landmark: String, type: PlaceType) {
        viewModelScope.launch {
            locusRepository.registerPlace(
                Place(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    landmarkAnchor = landmark,
                    type = type
                )
            )
            _uiState.update { it.copy(showAddPlaceDialog = false) }
        }
    }

    fun getAlertsForPlace(placeId: String): List<PraxisAlert> =
        _uiState.value.activeAlerts.filter { it.triggeringPlaceIds.contains(placeId) }
}
