package ai.or4cl3.meridian.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * LOCUS Season Node
 *
 * Temporal markers anchoring observations to the ecological calendar.
 * Stores both Gregorian dates and traditional calendar position
 * (community-defined seasonal markers like wildlife events or plant indicators).
 */
@Entity(tableName = "seasons")
data class Season(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val label: String, // e.g. "Long Rains 2025"
    val startDate: Long,
    val endDate: Long? = null, // null if in progress
    val rainfallAssessment: RainfallAssessment = RainfallAssessment.UNKNOWN,
    val notes: String = "",
    val traditionalCalendarPosition: String = "", // e.g. "Begins with first Acacia flowering"
    val yieldOutcomeSummary: String = "", // populated at season close
    val isCurrent: Boolean = false
)

enum class RainfallAssessment {
    ABOVE_AVERAGE,
    NORMAL,
    BELOW_AVERAGE,
    DROUGHT,
    FLOOD,
    UNKNOWN
}
