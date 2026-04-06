package ai.or4cl3.meridian.data.repository

import ai.or4cl3.meridian.data.dao.*
import ai.or4cl3.meridian.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LocusRepository
 *
 * Single source of truth for all LOCUS knowledge graph operations.
 * ViewModels and use cases interact only with this repository.
 * All IO operations run on the injected dispatcher (Dispatchers.IO in production).
 */
@Singleton
class LocusRepository @Inject constructor(
    private val placeDao: PlaceDao,
    private val observationDao: ObservationDao,
    private val alertDao: AlertDao,
    private val interventionDao: InterventionDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    // ---- Places ----

    fun observeAllPlaces(): Flow<List<Place>> = placeDao.observeAllPlaces()

    suspend fun getPlaceById(id: String): Place? =
        withContext(dispatcher) { placeDao.getPlaceById(id) }

    suspend fun registerPlace(place: Place) =
        withContext(dispatcher) { placeDao.insertPlace(place) }

    suspend fun updatePlace(place: Place) =
        withContext(dispatcher) { placeDao.updatePlace(place) }

    // ---- Observations ----

    fun observeRecentObservations(limit: Int = 50): Flow<List<Observation>> =
        observationDao.observeRecentObservations(limit)

    fun observeObservationsForPlace(placeId: String): Flow<List<Observation>> =
        observationDao.observeObservationsForPlace(placeId)

    suspend fun recordObservation(observation: Observation) = withContext(dispatcher) {
        observationDao.insertObservation(observation)
        placeDao.updateLastObserved(observation.placeId, observation.timestamp)
    }

    /**
     * PRAXIS temporal context query.
     * Returns all observations for a place since the start of the earliest
     * recorded season — the full historical record for pattern analysis.
     */
    suspend fun getHistoricalObservations(
        placeId: String,
        category: ObservationCategory,
        lookbackMs: Long = 3L * 365 * 24 * 60 * 60 * 1000 // 3 years default
    ): List<Observation> = withContext(dispatcher) {
        observationDao.getObservationsForPatternAnalysis(
            placeId = placeId,
            fromTime = System.currentTimeMillis() - lookbackMs
        ).filter { it.category == category }
    }

    suspend fun getUnsyncedObservations(): List<Observation> =
        withContext(dispatcher) { observationDao.getUnsynced() }

    suspend fun markObservationsSynced(ids: List<String>) =
        withContext(dispatcher) { observationDao.markSynced(ids) }

    // ---- PRAXIS Alerts ----

    fun observeActiveAlerts(): Flow<List<PraxisAlert>> = alertDao.observeActiveAlerts()

    fun observeUnreadAlertCount(): Flow<Int> = alertDao.observeUnreadCount()

    suspend fun saveAlert(alert: PraxisAlert) =
        withContext(dispatcher) { alertDao.insertAlert(alert) }

    suspend fun markAlertRead(id: String) =
        withContext(dispatcher) { alertDao.markRead(id) }

    suspend fun resolveAlert(id: String, interventionId: String? = null) =
        withContext(dispatcher) { alertDao.resolveAlert(id, interventionId) }

    // ---- Interventions & Outcomes ----

    fun observeInterventionsForPlace(placeId: String): Flow<List<Intervention>> =
        interventionDao.observeInterventionsForPlace(placeId)

    suspend fun recordIntervention(intervention: Intervention): Long =
        withContext(dispatcher) { interventionDao.insertIntervention(intervention) }

    suspend fun recordOutcome(outcome: Outcome): Long =
        withContext(dispatcher) { interventionDao.insertOutcome(outcome) }

    /**
     * Retrieves outcome history for a place.
     * PRAXIS uses this to calibrate recommendation confidence
     * based on what has actually worked for this specific community.
     */
    suspend fun getOutcomesForPlace(placeId: String): List<Outcome> =
        withContext(dispatcher) { interventionDao.getOutcomesForPlace(placeId) }
}
