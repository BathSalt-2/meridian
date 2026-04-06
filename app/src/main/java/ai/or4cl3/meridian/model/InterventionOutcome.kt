package ai.or4cl3.meridian.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * LOCUS Intervention Node
 *
 * Records actions taken by the community in response to observations
 * or PRAXIS recommendations. Links back to the triggering observation
 * and alert, and forward to outcome nodes.
 */
@Entity(
    tableName = "interventions",
    foreignKeys = [
        ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("placeId"), Index("observationId"), Index("alertId")]
)
data class Intervention(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val observationId: String? = null, // triggering observation, if any
    val alertId: String? = null, // triggering PRAXIS alert, if any
    val placeId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String,
    val resourcesUsed: String, // JSON array
    val appliedByRole: String = "", // anonymized role label
    val notes: String = "",
    val outcomeId: String? = null // set when outcome is recorded
)

/**
 * LOCUS Outcome Node
 *
 * Result of an intervention. Closes the PRAXIS feedback loop:
 * Observation → Alert → Intervention → Outcome → back into LOCUS history.
 */
@Entity(
    tableName = "outcomes",
    foreignKeys = [
        ForeignKey(
            entity = Intervention::class,
            parentColumns = ["id"],
            childColumns = ["interventionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("interventionId")]
)
data class Outcome(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val interventionId: String,
    val assessedAt: Long = System.currentTimeMillis(),
    val result: OutcomeResult,
    val yieldImpactEstimate: String = "", // qualitative
    val resourceCostEstimate: String = "", // qualitative
    val communityNotes: String = "",
    val contributedToCommons: Boolean = false
)

enum class OutcomeResult {
    RESOLVED,
    IMPROVED,
    UNCHANGED,
    WORSENED,
    PENDING
}
