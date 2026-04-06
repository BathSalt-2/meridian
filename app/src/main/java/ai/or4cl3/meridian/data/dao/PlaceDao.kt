package ai.or4cl3.meridian.data.dao

import ai.or4cl3.meridian.model.Place
import ai.or4cl3.meridian.model.PlaceType
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Query("SELECT * FROM places WHERE isActive = 1 ORDER BY registeredAt DESC")
    fun observeAllPlaces(): Flow<List<Place>>

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlaceById(id: String): Place?

    @Query("SELECT * FROM places WHERE type = :type AND isActive = 1 ORDER BY name ASC")
    fun observePlacesByType(type: PlaceType): Flow<List<Place>>

    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%' OR landmarkAnchor LIKE '%' || :query || '%'")
    suspend fun searchPlaces(query: String): List<Place>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<Place>)

    @Update
    suspend fun updatePlace(place: Place)

    @Query("UPDATE places SET lastObserved = :timestamp WHERE id = :placeId")
    suspend fun updateLastObserved(placeId: String, timestamp: Long)

    @Query("UPDATE places SET isActive = 0 WHERE id = :id")
    suspend fun archivePlace(id: String)

    @Query("SELECT COUNT(*) FROM places WHERE isActive = 1")
    suspend fun getActivePlaceCount(): Int
}
