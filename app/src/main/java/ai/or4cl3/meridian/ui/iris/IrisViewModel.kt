package ai.or4cl3.meridian.ui.iris

import ai.or4cl3.meridian.ai.IrisAnalyzer
import ai.or4cl3.meridian.ai.ObservationTask
import ai.or4cl3.meridian.data.repository.LocusRepository
import ai.or4cl3.meridian.model.*
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class IrisMode { IDLE, CAPTURING, ANALYZING, RESULT, ERROR }

data class IrisUiState(
    val mode: IrisMode = IrisMode.IDLE,
    val selectedCategory: ObservationCategory = ObservationCategory.CROP_HEALTH,
    val capturedBitmap: Bitmap? = null,
    val analysisResult: IrisAnalyzer.IrisAnalysisResult? = null,
    val observationProtocol: List<ObservationTask> = emptyList(),
    val places: List<Place> = emptyList(),
    val selectedPlaceId: String? = null,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class IrisViewModel @Inject constructor(
    private val irisAnalyzer: IrisAnalyzer,
    private val locusRepository: LocusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IrisUiState())
    val uiState: StateFlow<IrisUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            locusRepository.observeAllPlaces().collect { places ->
                _uiState.update { it.copy(places = places) }
            }
        }
    }

    fun onImageCaptured(bitmap: Bitmap) {
        _uiState.update { it.copy(capturedBitmap = bitmap, mode = IrisMode.ANALYZING) }
        analyzeImage(bitmap)
    }

    private fun analyzeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            val placeId = _uiState.value.selectedPlaceId
            val history = if (placeId != null) {
                locusRepository.getHistoricalObservations(
                    placeId = placeId,
                    category = _uiState.value.selectedCategory
                )
            } else emptyList()

            val result = when (_uiState.value.selectedCategory) {
                ObservationCategory.CROP_HEALTH -> irisAnalyzer.analyzeCrop(
                    image = bitmap,
                    placeHistory = history
                )
                ObservationCategory.SOIL -> irisAnalyzer.analyzeSoil(
                    image = bitmap,
                    placeHistory = history
                )
                ObservationCategory.WATER_QUALITY -> irisAnalyzer.analyzeWaterSource(bitmap)
                else -> irisAnalyzer.analyzeCrop(image = bitmap)
            }

            result.fold(
                onSuccess = { analysisResult ->
                    _uiState.update {
                        it.copy(
                            mode = IrisMode.RESULT,
                            analysisResult = analysisResult,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            mode = IrisMode.ERROR,
                            errorMessage = error.message ?: "Analysis failed"
                        )
                    }
                }
            )
        }
    }

    fun saveObservation(deviceId: String) {
        val state = _uiState.value
        val result = state.analysisResult ?: return
        val placeId = state.selectedPlaceId ?: return

        viewModelScope.launch {
            val observation = Observation(
                id = UUID.randomUUID().toString(),
                placeId = placeId,
                category = result.category,
                classification = result.primaryClassification,
                confidence = result.confidence,
                severityStage = result.severityStage,
                evidenceChain = result.evidenceChain,
                communityNotes = "",
                deviceId = deviceId
            )
            locusRepository.recordObservation(observation)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun setCategory(category: ObservationCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSelectedPlace(placeId: String) {
        _uiState.update { it.copy(selectedPlaceId = placeId) }
    }

    fun reset() {
        _uiState.update { IrisUiState(places = it.places) }
    }
}
