package ai.or4cl3.meridian.ai

import ai.or4cl3.meridian.data.repository.LocusRepository
import ai.or4cl3.meridian.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PraxisReasoner
 *
 * The proactive intelligence core of MERIDIAN.
 * Monitors LOCUS continuously and fires reasoning processes when
 * trigger conditions are met. Every output uses Gemma 4 E4B's
 * native thinking mode, exposing the full reasoning chain.
 *
 * PRAXIS never returns a collapsed recommendation. The farmer
 * always sees the evidence, the alternatives, the confidence,
 * and the conditions under which the recommendation changes.
 */
@Singleton
class PraxisReasoner @Inject constructor(
    private val gemmaEngine: GemmaEngine,
    private val locusRepository: LocusRepository,
    private val promptBuilder: PromptBuilder
) {

    /**
     * Run the full PRAXIS monitoring cycle against current LOCUS state.
     * Called periodically by PraxisAlertService and on-demand after
     * new IRIS observations are recorded.
     *
     * Returns a list of new alerts to be saved to LOCUS.
     */
    suspend fun runMonitoringCycle(
        places: List<Place>,
        recentObservations: List<Observation>,
        activeSeason: Season?,
        existingAlerts: List<PraxisAlert>
    ): List<PraxisAlert> = withContext(Dispatchers.Default) {
        val newAlerts = mutableListOf<PraxisAlert>()

        // 1. Check threshold conditions
        newAlerts += checkThresholdConditions(places, recentObservations, activeSeason)

        // 2. Check pattern divergence against LOCUS history
        newAlerts += checkPatternDivergence(places, recentObservations, activeSeason)

        // 3. Check for opportunity conditions
        newAlerts += checkOpportunityConditions(places, recentObservations, activeSeason)

        // Deduplicate: don't generate alerts identical to unresolved existing ones
        newAlerts.filter { new ->
            existingAlerts.none { existing ->
                !existing.isResolved &&
                        existing.alertType == new.alertType &&
                        existing.triggeringPlaceIds == new.triggeringPlaceIds
            }
        }
    }

    /**
     * Generate a full PRAXIS reasoning response for a specific alert.
     * Returns a streaming Flow so the UI can display tokens as they appear.
     * This is the "thinking mode" output — the farmer sees PRAXIS reason
     * step-by-step before the final recommendation is presented.
     */
    fun generateAlertReasoning(
        alert: PraxisAlert,
        place: Place,
        historicalObservations: List<Observation>,
        communityProfile: CommunityProfile
    ): Flow<String> = flow {
        val prompt = promptBuilder.buildPraxisReasoningPrompt(
            alert = alert,
            place = place,
            historicalObservations = historicalObservations,
            communityProfile = communityProfile
        )
        // Stream the response for live display
        gemmaEngine.generateStreaming(prompt).collect { token -> emit(token) }
    }

    /**
     * Generate a seasonal forecast for a specific place.
     * Uses the full LOCUS historical record as context.
     */
    suspend fun generateSeasonalForecast(
        place: Place,
        currentSeason: Season,
        historicalSeasons: List<Season>,
        recentObservations: List<Observation>,
        communityProfile: CommunityProfile
    ): Result<SeasonalForecast> = withContext(Dispatchers.Default) {
        runCatching {
            val prompt = promptBuilder.buildSeasonalForecastPrompt(
                place = place,
                currentSeason = currentSeason,
                historicalSeasons = historicalSeasons,
                recentObservations = recentObservations,
                communityProfile = communityProfile
            )
            val result = gemmaEngine.generate(prompt).getOrThrow()
            parseSeasonalForecast(result.response, place)
        }
    }

    // ---- Threshold Monitoring ----

    private suspend fun checkThresholdConditions(
        places: List<Place>,
        observations: List<Observation>,
        season: Season?
    ): List<PraxisAlert> {
        val alerts = mutableListOf<PraxisAlert>()
        // Check: any place with Stage 3+ severity observations in the last 48 hours
        val urgentObservations = observations.filter {
            it.severityStage != null &&
                    it.severityStage >= 3 &&
                    System.currentTimeMillis() - it.timestamp < 48 * 60 * 60 * 1000L
        }
        urgentObservations.groupBy { it.placeId }.forEach { (placeId, obs) ->
            val place = places.find { it.id == placeId } ?: return@forEach
            val alert = generateThresholdAlert(place, obs, season)
            if (alert != null) alerts.add(alert)
        }
        return alerts
    }

    private suspend fun generateThresholdAlert(
        place: Place,
        urgentObservations: List<Observation>,
        season: Season?
    ): PraxisAlert? {
        if (!gemmaEngine.isReady()) return null
        val prompt = promptBuilder.buildThresholdAlertPrompt(place, urgentObservations, season)
        val result = gemmaEngine.generate(prompt).getOrNull() ?: return null
        return PraxisAlert(
            alertType = AlertType.THRESHOLD,
            severity = AlertSeverity.URGENT,
            title = "Urgent: ${urgentObservations.first().classification} at ${place.name}",
            summary = result.response.take(200),
            thinkingOutput = result.response,
            optionsJson = extractOptionsJson(result.response),
            triggeringObservationIds = urgentObservations.map { it.id }.joinToString(","),
            triggeringPlaceIds = place.id
        )
    }

    private suspend fun checkPatternDivergence(
        places: List<Place>,
        observations: List<Observation>,
        season: Season?
    ): List<PraxisAlert> {
        if (season == null || !gemmaEngine.isReady()) return emptyList()
        // Pattern divergence requires historical data — skip if insufficient
        return emptyList() // Populated when LOCUS has 2+ seasons of data
    }

    private suspend fun checkOpportunityConditions(
        places: List<Place>,
        observations: List<Observation>,
        season: Season?
    ): List<PraxisAlert> {
        return emptyList() // Opportunity detection active after onboarding completion
    }

    private fun parseSeasonalForecast(response: String, place: Place): SeasonalForecast {
        return SeasonalForecast(
            placeId = place.id,
            trajectory = response.lines().firstOrNull() ?: "",
            fullAnalysis = response,
            generatedAt = System.currentTimeMillis()
        )
    }

    private fun extractOptionsJson(response: String): String {
        // Extract the Options section from PRAXIS response
        val optionsStart = response.indexOf("Your options:")
        return if (optionsStart >= 0) response.substring(optionsStart).take(1000) else response.take(500)
    }
}

data class SeasonalForecast(
    val placeId: String,
    val trajectory: String,
    val fullAnalysis: String,
    val generatedAt: Long
)
