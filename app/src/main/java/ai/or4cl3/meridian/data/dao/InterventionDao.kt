package ai.or4cl3.meridian.data.dao

import ai.or4cl3.meridian.model.Intervention
import ai.or4cl3.meridian.model.Outcome
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InterventionDao {

    @Query("SELECT * FROM interventions ORDER BY timestamp DESC")
    fun observeAllInterventions(): Flow<List<Intervention>>

    @Query("SELECT * FROM interventions WHERE placeId = :placeId ORDER BY timestamp DESC")
    fun observeInterventionsForPlace(placeId: String): Flow<List<Intervention>>

    @Query("SELECT * FROM interventions WHERE alertId = :alertId")
    suspend fun getInterventionsForAlert(alertId: String): List<Intervention>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntervention(intervention: Intervention): Long

    @Update
    suspend fun updateIntervention(intervention: Intervention)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutcome(outcome: Outcome): Long

    @Query("SELECT * FROM outcomes WHERE interventionId = :interventionId")
    suspend fun getOutcomeForIntervention(interventionId: String): Outcome?

    @Query("SELECT * FROM outcomes ORDER BY assessedAt DESC LIMIT :limit")
    fun observeRecentOutcomes(limit: Int = 50): Flow<List<Outcome>>

    // PRAXIS feedback: retrieve outcome data for recommendation calibration
    @Query(
        """SELECT o.* FROM outcomes o
        INNER JOIN interventions i ON o.interventionId = i.id
        WHERE i.placeId = :placeId
        ORDER BY o.assessedAt DESC"""
    )
    suspend fun getOutcomesForPlace(placeId: String): List<Outcome>
}
