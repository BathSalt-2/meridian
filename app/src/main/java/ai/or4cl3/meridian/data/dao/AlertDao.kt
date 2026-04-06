package ai.or4cl3.meridian.data.dao

import ai.or4cl3.meridian.model.AlertType
import ai.or4cl3.meridian.model.PraxisAlert
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM praxis_alerts WHERE isResolved = 0 ORDER BY generatedAt DESC")
    fun observeActiveAlerts(): Flow<List<PraxisAlert>>

    @Query("SELECT * FROM praxis_alerts ORDER BY generatedAt DESC LIMIT :limit")
    fun observeAllAlerts(limit: Int = 100): Flow<List<PraxisAlert>>

    @Query("SELECT * FROM praxis_alerts WHERE id = :id")
    suspend fun getAlertById(id: String): PraxisAlert?

    @Query("SELECT * FROM praxis_alerts WHERE alertType = :type AND isResolved = 0 ORDER BY generatedAt DESC")
    fun observeAlertsByType(type: AlertType): Flow<List<PraxisAlert>>

    @Query("SELECT COUNT(*) FROM praxis_alerts WHERE isRead = 0 AND isResolved = 0")
    fun observeUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PraxisAlert)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<PraxisAlert>)

    @Query("UPDATE praxis_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE praxis_alerts SET isRead = 1")
    suspend fun markAllRead()

    @Query("UPDATE praxis_alerts SET isResolved = 1, linkedInterventionId = :interventionId WHERE id = :id")
    suspend fun resolveAlert(id: String, interventionId: String? = null)

    @Query("SELECT * FROM praxis_alerts WHERE isResolved = 0 AND (expiresAt IS NULL OR expiresAt > :now)")
    suspend fun getNonExpiredActiveAlerts(now: Long = System.currentTimeMillis()): List<PraxisAlert>
}
