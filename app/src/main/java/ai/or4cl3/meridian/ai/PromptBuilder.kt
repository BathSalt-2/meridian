package ai.or4cl3.meridian.ai

import ai.or4cl3.meridian.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PromptBuilder
 *
 * Constructs structured prompts for IRIS and PRAXIS.
 * All prompts follow a consistent architecture:
 *   1. System role definition
 *   2. Context injection (LOCUS history, community profile)
 *   3. Task instruction
 *   4. Output format specification
 *
 * The output format enforces IRIS's differential diagnosis structure:
 * Primary finding, confidence, evidence, alternatives, and follow-up.
 *
 * PRAXIS prompts use thinking mode markers (<thinking>...</thinking>)
 * to trigger step-by-step reasoning before the final recommendation.
 */
@Singleton
class PromptBuilder @Inject constructor() {

    companion object {
        private const val IRIS_SYSTEM_ROLE = """
You are IRIS — the Integrated Reality Intelligence Scanner for MERIDIAN, 
an ecological intelligence system for smallholder farming communities.
You analyze agricultural observations with rigorous differential reasoning.
You ALWAYS present multiple hypotheses with confidence scores.
You NEVER collapse to a single answer without showing your reasoning.
You cite specific visual or sensory features that support each hypothesis.
You suggest follow-up observations to distinguish between candidates.
You recommend only interventions feasible with locally available resources.
You are honest about the limits of visual analysis — you flag when a
physical test or laboratory confirmation is needed."""

        private const val PRAXIS_SYSTEM_ROLE = """
You are PRAXIS — the Predictive Resilience and Adaptive Expert Intelligence
System for MERIDIAN. You reason about agricultural and ecological situations
facing smallholder farming communities.
You ALWAYS reason step by step before providing a recommendation.
You expose your reasoning chain transparently — the farmer must see WHY
you are making a recommendation, not just what it is.
You provide options in three tiers: optimal, achievable, and minimal.
You calibrate recommendations to the community's actual available resources.
You are honest about uncertainty and the conditions that would change your view.
You treat traditional ecological knowledge as equal-weight evidence alongside
agronomic science. When they conflict, you present both views explicitly."""
    }

    // ---- IRIS Prompts ----

    fun buildCropAnalysisPrompt(
        cropHint: String?,
        recentObservations: List<Observation>,
        season: Season?
    ): String = buildString {
        appendLine(IRIS_SYSTEM_ROLE)
        appendLine()
        if (season != null) {
            appendLine("Current season: ${season.label}")
            appendLine("Rainfall: ${season.rainfallAssessment.name.lowercase().replace('_', ' ')}")
        }
        if (recentObservations.isNotEmpty()) {
            appendLine("\nRecent observations at this location:")
            recentObservations.takeLast(3).forEach { obs ->
                appendLine("  - ${obs.category.name}: ${obs.classification} (confidence ${(obs.confidence * 100).toInt()}%)")
            }
        }
        if (cropHint != null) appendLine("\nCrop: $cropHint")
        appendLine()
        appendLine("Analyze this crop photograph. Provide:")
        appendLine("1. PRIMARY FINDING: [condition] (Confidence: X%)")
        appendLine("   Evidence: [specific visual features observed]")
        appendLine("2. ALTERNATIVE: [condition] (Confidence: X%)")
        appendLine("   Evidence: [supporting features]")
        appendLine("   Distinguishing test: [how to confirm or rule out]")
        appendLine("3. Severity: Stage X of 5 — [brief description]")
        appendLine("4. Recommended next actions:")
        appendLine("   - [action 1 using locally available resources]")
        appendLine("   - [action 2]")
        appendLine("5. What would cause you to revise this assessment: [conditions]")
    }

    fun buildSoilAnalysisPrompt(recentObservations: List<Observation>): String = buildString {
        appendLine(IRIS_SYSTEM_ROLE)
        appendLine()
        if (recentObservations.isNotEmpty()) {
            appendLine("Previous soil observations at this location:")
            recentObservations.forEach { obs ->
                appendLine("  - ${obs.classification} (${obs.confidence * 100}% confidence)")
            }
        }
        appendLine()
        appendLine("Analyze this soil photograph. Evaluate:")
        appendLine("1. ORGANIC MATTER: [low/moderate/high] (Confidence: X%)")
        appendLine("   Evidence: [color, structure, surface characteristics]")
        appendLine("2. COMPACTION: [none/mild/moderate/severe] (Confidence: X%)")
        appendLine("   Evidence: [visual indicators]")
        appendLine("3. DRAINAGE: [well-drained/moderate/poorly drained] (Confidence: X%)")
        appendLine("4. EROSION: Stage X of 5 (Confidence: X%)")
        appendLine("5. PRIMARY CONCERN: [most urgent soil issue]")
        appendLine("6. Recommended amendment (using locally available materials):")
        appendLine("   - [amendment 1]")
        appendLine("Note: These are visual proxies. Recommend the jar-shake test if clay content precision is needed.")
    }

    fun buildWaterAnalysisPrompt(): String = buildString {
        appendLine(IRIS_SYSTEM_ROLE)
        appendLine()
        appendLine("Analyze this water source photograph. Classify into one category:")
        appendLine("CATEGORY A — LOW RISK: Clear water, clean banks, no surface film")
        appendLine("CATEGORY B — MODERATE RISK: Mild turbidity or some bank disturbance")
        appendLine("CATEGORY C — ELEVATED RISK: Heavy turbidity, surface film, or algae")
        appendLine("CATEGORY D — HIGH RISK: Discoloration, strong odor indicators, or dead vegetation")
        appendLine()
        appendLine("Provide:")
        appendLine("1. CLASSIFICATION: Category [A/B/C/D] (Confidence: X%)")
        appendLine("   Evidence: [specific visual indicators]")
        appendLine("2. Probable contamination type (if C or D): [agricultural/industrial/sewage/natural mineral]")
        appendLine("3. Safe uses: [crop irrigation / livestock / neither]")
        appendLine("4. Field confirmation test recommended: [low-tech test description]")
        appendLine("5. Treatment if needed: [feasible options]")
    }

    fun buildVoiceObservationPrompt(
        observation: String,
        category: ObservationCategory,
        history: List<Observation>
    ): String = buildString {
        appendLine(IRIS_SYSTEM_ROLE)
        appendLine()
        appendLine("A farmer has described the following observation (transcribed from voice):")
        appendLine("\"$observation\"")
        appendLine()
        if (history.isNotEmpty()) {
            appendLine("Recent history at this location:")
            history.take(3).forEach { obs ->
                appendLine("  - ${obs.classification}")
            }
        }
        appendLine()
        appendLine("Based on this verbal description, provide your best assessment:")
        appendLine("1. PRIMARY FINDING: [condition] (Confidence: X%)")
        appendLine("2. Key uncertainty: [what you cannot assess without visual confirmation]")
        appendLine("3. Recommended observation: [what to photograph to confirm]")
        appendLine("4. Immediate action (if any): [what to do now without waiting for visual confirmation]")
    }

    fun buildObservationProtocolPrompt(
        places: List<Place>,
        activeAlerts: List<PraxisAlert>,
        season: Season?
    ): String = buildString {
        appendLine(IRIS_SYSTEM_ROLE)
        appendLine()
        appendLine("Generate a prioritized observation checklist for today's farm walk.")
        if (season != null) appendLine("Current season: ${season.label}")
        appendLine("\nRegistered places (${places.size}):")
        places.take(6).forEach { p -> appendLine("  - ${p.name} (${p.type.name.lowercase()})") }
        if (activeAlerts.isNotEmpty()) {
            appendLine("\nActive PRAXIS alerts requiring monitoring:")
            activeAlerts.take(3).forEach { a -> appendLine("  - ${a.title}") }
        }
        appendLine("\nProvide 5-8 specific observation tasks, in priority order:")
        appendLine("Format: [number]. [specific task at specific place] — [what to look for]")
    }

    // ---- PRAXIS Prompts ----

    fun buildPraxisReasoningPrompt(
        alert: PraxisAlert,
        place: Place,
        historicalObservations: List<Observation>,
        communityProfile: CommunityProfile
    ): String = buildString {
        appendLine(PRAXIS_SYSTEM_ROLE)
        appendLine()
        appendLine("<thinking>")
        appendLine("Reason through this situation step by step before responding.")
        appendLine("</thinking>")
        appendLine()
        appendLine("ALERT: ${alert.title}")
        appendLine("Location: ${place.name} (${place.landmarkAnchor})")
        appendLine("Alert type: ${alert.alertType.name}")
        appendLine()
        appendLine("LOCUS historical context (${historicalObservations.size} observations):")
        historicalObservations.takeLast(5).forEach { obs ->
            appendLine("  [${formatTimestamp(obs.timestamp)}] ${obs.classification} — Stage ${obs.severityStage ?: "N/A"}")
        }
        appendLine()
        appendLine("Community resources: ${communityProfile.availableLocalInputs.joinToString(", ")}")
        appendLine("Market access: ${communityProfile.nearestMarketDistanceKm}km, every ${communityProfile.marketFrequencyDays} days")
        appendLine()
        appendLine("Provide:")
        appendLine("What I observed: [the specific signals that triggered this alert]")
        appendLine("What pattern I identified: [the LOCUS history context]")
        appendLine("What this suggests: [probability assessment]")
        appendLine("What I am uncertain about: [honest uncertainty]")
        appendLine("Your options:")
        appendLine("  Option 1 — Optimal: [best outcome, any resources]")
        appendLine("  Option 2 — Achievable: [best with available resources]")
        appendLine("  Option 3 — Minimal: [lowest resource intervention]")
        appendLine("What would change this recommendation: [conditions for revision]")
    }

    fun buildThresholdAlertPrompt(
        place: Place,
        urgentObservations: List<Observation>,
        season: Season?
    ): String = buildString {
        appendLine(PRAXIS_SYSTEM_ROLE)
        appendLine()
        appendLine("URGENT THRESHOLD CONDITION DETECTED")
        appendLine("Location: ${place.name}")
        if (season != null) appendLine("Season: ${season.label}")
        appendLine()
        appendLine("Triggering observations:")
        urgentObservations.forEach { obs ->
            appendLine("  - ${obs.classification} (Stage ${obs.severityStage}/5, ${(obs.confidence * 100).toInt()}% confidence)")
        }
        appendLine()
        appendLine("Generate a concise alert. Lead with the most critical information.")
        appendLine("Provide 3 tiered response options (optimal / achievable / minimal).")
        appendLine("Be specific about timing — what must be done today vs this week.")
    }

    fun buildSeasonalForecastPrompt(
        place: Place,
        currentSeason: Season,
        historicalSeasons: List<Season>,
        recentObservations: List<Observation>,
        communityProfile: CommunityProfile
    ): String = buildString {
        appendLine(PRAXIS_SYSTEM_ROLE)
        appendLine()
        appendLine("Generate a seasonal forecast for ${place.name}.")
        appendLine("Current season: ${currentSeason.label} (${currentSeason.rainfallAssessment.name})")
        appendLine()
        if (historicalSeasons.isNotEmpty()) {
            appendLine("Historical seasons for comparison:")
            historicalSeasons.take(4).forEach { s ->
                appendLine("  - ${s.label}: ${s.rainfallAssessment.name} — ${s.yieldOutcomeSummary.take(80)}")
            }
        }
        appendLine()
        appendLine("Recent observations (${recentObservations.size}):")
        recentObservations.takeLast(5).forEach { obs ->
            appendLine("  - ${obs.classification} (${(obs.confidence * 100).toInt()}%)")
        }
        appendLine()
        appendLine("Provide:")
        appendLine("TRAJECTORY: [current-season direction]")
        appendLine("YIELD PROJECTION: [most likely / optimistic / pessimistic]")
        appendLine("KEY RISKS for remainder of season: [top 3]")
        appendLine("NEXT-SEASON PREPARATION: [top 3 priority actions]")
    }

    private fun formatTimestamp(timestamp: Long): String {
        val daysAgo = (System.currentTimeMillis() - timestamp) / (24 * 60 * 60 * 1000)
        return when {
            daysAgo == 0L -> "today"
            daysAgo == 1L -> "yesterday"
            daysAgo < 30 -> "${daysAgo}d ago"
            daysAgo < 365 -> "${daysAgo / 30}mo ago"
            else -> "${daysAgo / 365}yr ago"
        }
    }
}
