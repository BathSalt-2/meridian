package ai.or4cl3.meridian.ai

import ai.or4cl3.meridian.model.*
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * IrisAnalyzer
 *
 * Converts raw sensory inputs (images, audio descriptions, voice text)
 * into structured Observation records via Gemma 4 E4B multimodal inference.
 *
 * Key design principle: IRIS always returns a differential diagnosis,
 * never a single collapsed answer. The farmer sees confidence scores,
 * evidence chains, and alternative hypotheses — not just a conclusion.
 */
@Singleton
class IrisAnalyzer @Inject constructor(
    private val gemmaEngine: GemmaEngine,
    private val promptBuilder: PromptBuilder
) {

    data class IrisAnalysisResult(
        val category: ObservationCategory,
        val primaryClassification: String,
        val confidence: Float,
        val severityStage: Int?,
        val evidenceChain: String, // Full IRIS reasoning output (JSON)
        val differentialDiagnoses: List<DiagnosisCandidate>,
        val followUpActions: List<String>,
        val rawResponse: String
    )

    data class DiagnosisCandidate(
        val classification: String,
        val confidence: Float,
        val supportingEvidence: List<String>,
        val distinguishingTest: String? = null
    )

    /**
     * Analyze a crop photograph for disease, pest damage, or stress.
     * Returns a differential diagnosis with full evidence chain.
     */
    suspend fun analyzeCrop(
        image: Bitmap,
        cropHint: String? = null,
        placeHistory: List<Observation> = emptyList(),
        season: Season? = null
    ): Result<IrisAnalysisResult> = withContext(Dispatchers.Default) {
        runCatching {
            val prompt = promptBuilder.buildCropAnalysisPrompt(
                cropHint = cropHint,
                recentObservations = placeHistory.takeLast(5),
                season = season
            )
            val result = gemmaEngine.generateWithImage(prompt, image).getOrThrow()
            parseCropAnalysisResponse(result.response, ObservationCategory.CROP_HEALTH)
        }
    }

    /**
     * Analyze a soil photograph for health indicators.
     * Visual proxies: color, texture, compaction, erosion, surface crust.
     */
    suspend fun analyzeSoil(
        image: Bitmap,
        placeHistory: List<Observation> = emptyList()
    ): Result<IrisAnalysisResult> = withContext(Dispatchers.Default) {
        runCatching {
            val prompt = promptBuilder.buildSoilAnalysisPrompt(placeHistory.takeLast(3))
            val result = gemmaEngine.generateWithImage(prompt, image).getOrThrow()
            parseCropAnalysisResponse(result.response, ObservationCategory.SOIL)
        }
    }

    /**
     * Analyze a water source photograph for risk classification.
     * Returns one of four risk categories with contamination type hypothesis.
     */
    suspend fun analyzeWaterSource(
        image: Bitmap
    ): Result<IrisAnalysisResult> = withContext(Dispatchers.Default) {
        runCatching {
            val prompt = promptBuilder.buildWaterAnalysisPrompt()
            val result = gemmaEngine.generateWithImage(prompt, image).getOrThrow()
            parseCropAnalysisResponse(result.response, ObservationCategory.WATER_QUALITY)
        }
    }

    /**
     * Analyze voice description of an observation.
     * Used in voice-first mode where the farmer describes what they see
     * rather than photographing it.
     */
    suspend fun analyzeVoiceObservation(
        transcribedText: String,
        category: ObservationCategory,
        placeHistory: List<Observation> = emptyList()
    ): Result<IrisAnalysisResult> = withContext(Dispatchers.Default) {
        runCatching {
            val prompt = promptBuilder.buildVoiceObservationPrompt(
                observation = transcribedText,
                category = category,
                history = placeHistory.takeLast(5)
            )
            val result = gemmaEngine.generate(prompt).getOrThrow()
            parseCropAnalysisResponse(result.response, category)
        }
    }

    /**
     * Generate a guided observation checklist for the current session.
     * IRIS uses this to tell the farmer exactly what to photograph
     * based on the season, active alerts, and observation gaps.
     */
    suspend fun generateObservationProtocol(
        places: List<Place>,
        activePlaceIds: Set<String>,
        activeAlerts: List<PraxisAlert>,
        season: Season?
    ): Result<List<ObservationTask>> = withContext(Dispatchers.Default) {
        runCatching {
            val prompt = promptBuilder.buildObservationProtocolPrompt(
                places = places,
                activeAlerts = activeAlerts,
                season = season
            )
            val result = gemmaEngine.generate(prompt).getOrThrow()
            parseObservationProtocol(result.response, places)
        }
    }

    // ---- Response Parsers ----

    private fun parseCropAnalysisResponse(
        response: String,
        category: ObservationCategory
    ): IrisAnalysisResult {
        // Structured extraction from IRIS response format
        // Production: implement robust JSON extraction with fallback regex parsing
        val lines = response.lines().map { it.trim() }.filter { it.isNotBlank() }

        // Extract primary finding (first classification line)
        val primaryLine = lines.firstOrNull { it.startsWith("Primary:", ignoreCase = true) }
            ?: lines.firstOrNull() ?: "Unknown condition"
        val primaryClassification = primaryLine
            .removePrefix("Primary:")
            .removePrefix("PRIMARY FINDING:")
            .trim()
            .substringBefore("(")
            .trim()

        // Extract confidence
        val confidenceMatch = Regex("Confidence[:\\s]+(\\d+)%").find(response)
        val confidence = (confidenceMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 75f) / 100f

        // Extract severity
        val severityMatch = Regex("Stage\\s+(\\d)\\s+of\\s+5").find(response)
        val severity = severityMatch?.groupValues?.get(1)?.toIntOrNull()

        // Build differential candidates from response
        val alternatives = mutableListOf<DiagnosisCandidate>()
        val altPattern = Regex("Alternative[:\\s]+(.+?)\\(Confidence[:\\s]+(\\d+)%\\)")
        altPattern.findAll(response).forEach { match ->
            alternatives.add(
                DiagnosisCandidate(
                    classification = match.groupValues[1].trim(),
                    confidence = (match.groupValues[2].toFloatOrNull() ?: 10f) / 100f,
                    supportingEvidence = listOf("See full response")
                )
            )
        }

        // Extract follow-up actions
        val actions = lines
            .filter { it.startsWith("-") || it.startsWith("•") }
            .map { it.removePrefix("-").removePrefix("•").trim() }
            .take(5)

        return IrisAnalysisResult(
            category = category,
            primaryClassification = primaryClassification,
            confidence = confidence,
            severityStage = severity,
            evidenceChain = response, // Full response preserved as evidence
            differentialDiagnoses = alternatives,
            followUpActions = actions,
            rawResponse = response
        )
    }

    private fun parseObservationProtocol(
        response: String,
        places: List<Place>
    ): List<ObservationTask> {
        // Parse structured task list from IRIS protocol response
        return response.lines()
            .filter { it.trim().isNotBlank() && (it.trim().first().isDigit() || it.startsWith("-")) }
            .take(8)
            .mapIndexed { index, line ->
                ObservationTask(
                    priority = index + 1,
                    description = line.removePrefix("-").trim().removePrefix("${index + 1}.").trim(),
                    suggestedCategory = ObservationCategory.CROP_HEALTH,
                    targetPlaceId = places.firstOrNull()?.id
                )
            }
    }
}

data class ObservationTask(
    val priority: Int,
    val description: String,
    val suggestedCategory: ObservationCategory,
    val targetPlaceId: String?
)
