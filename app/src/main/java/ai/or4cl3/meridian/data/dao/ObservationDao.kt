package ai.or4cl3.meridian.data.dao

import ai.or4cl3.meridian.model.Observation
import ai.or4cl3.meridian.model.ObservationCategory
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDao {

    @Query("SELECT * FROM observations ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentObservations(limit: Int = 50): Flow<List<Observation>>

    @Query("SELECT * FROM observations WHERE placeId = :placeId ORDER BY timestamp DESC")
    fun observeObservationsForPlace(placeId: String): Flow<List<Observation>>

    @Query(
        """SELECT * FROM observations 
        WHERE placeId = :placeId 
        AND category = :category 
        AND timestamp BETWEEN :fromTime AND :toTime 
        ORDER BY timestamp ASC"""
    )
    suspend fun getObservationsForPlaceInRange(
        placeId: String,
        category: ObservationCategory,
        fromTime: Long,
        toTime: Long
    ): List<Observation>

    @Query("SELECT * FROM observations WHERE category = :category ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getObservationsByCategory(category: ObservationCategory, limit: Int = 100): List<Observation>

    // PRAXIS temporal query: get observations for LOCUS historical pattern matching
    @Query(
        """SELECT * FROM observations 
        WHERE placeId = :placeId 
        AND timestamp >= :fromTime 
        ORDER BY timestamp DESC"""
    )
    suspend fun getObservationsForPatternAnalysis(placeId: String, fromTime: Long): List<Observation>

    @Query("SELECT * FROM observations WHERE id IN (:ids)")
    suspend fun getObservationsByIds(ids: List<String>): List<Observation>

    @Query("SELECT * FROM observations WHERE synced = 0")
    suspend fun getUnsynced(): List<Observation>

    @Query("UPDATE observations SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertObservation(observation: Observation): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertObservations(observations: List<Observation>)

    @Query("SELECT COUNT(*) FROM observations WHERE placeId = :placeId")
    suspend fun getObservationCountForPlace(placeId: String): Int

    @Query("SELECT COUNT(*) FROM observations WHERE timestamp >= :since")
    suspend fun getObservationCountSince(since: Long): Int

    @Query("DELETE FROM observations WHERE timestamp < :before AND synced = 1")
    suspend fun pruneOldSyncedObservations(before: Long): Int
}
