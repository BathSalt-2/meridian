package ai.or4cl3.meridian.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * LOCUS Species Node
 *
 * Biological subjects tracked by the community — crops, pests, pathogens,
 * beneficial species. Stores both scientific classification and community
 * traditional knowledge as first-class fields (not secondary annotations).
 */
@Entity(tableName = "species")
data class Species(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val commonName: String,
    val localNames: String, // JSON array of community language names
    val scientificName: String? = null,
    val type: SpeciesType,
    val varietyNotes: String = "",
    val traditionalKnowledge: String = "", // JSON: practices, indicators, relationships
    val affectedCropIds: String = "", // JSON array of species IDs (for pests/pathogens)
    val registeredAt: Long = System.currentTimeMillis()
)

enum class SpeciesType {
    CROP,
    PEST_INSECT,
    PEST_MAMMAL,
    PATHOGEN_FUNGAL,
    PATHOGEN_BACTERIAL,
    PATHOGEN_VIRAL,
    BENEFICIAL,
    WILD_PLANT,
    LIVESTOCK,
    TREE
}
