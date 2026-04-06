package ai.or4cl3.meridian.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * LOCUS Observation Node
 *
 * Timestamped record of an IRIS output anchored to a Place.
 * Every observation stores the full IRIS reasoning chain (evidenceChain)
 * to enable PRAXIS to reference original evidence when generating alerts.
 */
@Entity(
    tableName = "observations",
    foreignKeys = [
        ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("placeId"), Index("timestamp"), Index("category")]
)
data class Observation(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val placeId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: ObservationCategory,
    val classification: String,
    val confidence: Float, // 0.0 - 1.0
    val severityStage: Int? = null, // 1-5, where applicable
    val evidenceChain: String, // JSON: full IRIS reasoning output
    val thumbnailPath: String? = null, // local encrypted file path
    val audioPath: String? = null, // local encrypted file path
    val communityNotes: String = "",
    val deviceId: String,
    val synced: Boolean = false // tracks mesh sync status
)

enum class ObservationCategory {
    CROP_HEALTH,
    SOIL,
    WATER_QUALITY,
    DEFORESTATION,
    ACOUSTIC,
    PEST,
    WEATHER,
    MANUAL_NOTE
}
