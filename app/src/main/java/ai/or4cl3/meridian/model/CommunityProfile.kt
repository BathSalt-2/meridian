package ai.or4cl3.meridian.model

/**
 * CommunityProfile
 *
 * Stored in encrypted DataStore (not Room).
 * Contains community identity, resource profile, and system preferences.
 * This is the first structure populated during onboarding.
 */
data class CommunityProfile(
    val communityId: String,
    val communityName: String,
    val primaryLanguage: String,
    val additionalLanguages: List<String> = emptyList(),
    val region: String = "",
    val primaryCrops: List<String> = emptyList(),
    // Resource profile — inputs to PRAXIS recommendation filtering
    val availableLocalInputs: List<String> = emptyList(),
    val nearestMarketDistanceKm: Float? = null,
    val marketFrequencyDays: Int? = null,
    val laborCapacity: LaborCapacity = LaborCapacity.MODERATE,
    // Device info
    val deviceId: String,
    val isPrimaryDevice: Boolean = false,
    // Mesh
    val meshEnabled: Boolean = false,
    val psalMeshEnabled: Boolean = false,
    // Model config
    val preferredModel: ModelTier = ModelTier.E4B,
    // Setup
    val onboardingComplete: Boolean = false,
    val onboardingStep: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

enum class LaborCapacity { LOW, MODERATE, HIGH }
enum class ModelTier { E2B, E4B }
