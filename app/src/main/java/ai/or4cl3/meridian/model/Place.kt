package ai.or4cl3.meridian.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * LOCUS Place Node
 *
 * Geographic anchor for all LOCUS observations.
 * Uses community landmark grid instead of GPS to preserve data sovereignty
 * and eliminate dependency on cloud location services.
 */
@Entity(tableName = "places")
data class Place(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val landmarkAnchor: String, // e.g. "north of the baobab tree"
    val type: PlaceType,
    val areaEstimateHa: Float? = null,
    val notes: String = "",
    val registeredAt: Long = System.currentTimeMillis(),
    val lastObserved: Long? = null,
    val isActive: Boolean = true
)

enum class PlaceType {
    FIELD,
    WATER_SOURCE,
    FOREST_PATCH,
    GRAZING_AREA,
    INFRASTRUCTURE,
    HOMESTEAD
}
