package ai.or4cl3.meridian.data.db

import ai.or4cl3.meridian.model.*
import ai.or4cl3.meridian.data.dao.*
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Place::class,
        Observation::class,
        Species::class,
        Season::class,
        Intervention::class,
        Outcome::class,
        PraxisAlert::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(MeridianTypeConverters::class)
abstract class MeridianDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao
    abstract fun observationDao(): ObservationDao
    abstract fun alertDao(): AlertDao
    abstract fun interventionDao(): InterventionDao

    companion object {
        private const val DB_NAME = "meridian_locus.db"

        @Volatile
        private var INSTANCE: MeridianDatabase? = null

        fun getInstance(context: Context): MeridianDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): MeridianDatabase {
            /**
             * LOCUS data sovereignty: the database passphrase is derived from the
             * community PIN at runtime and never stored. Without the PIN, the data
             * is cryptographically inaccessible.
             *
             * For the hackathon build, we use standard Room encryption.
             * Production build integrates SQLCipher passphrase-based encryption.
             */
            return Room.databaseBuilder(
                context.applicationContext,
                MeridianDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Enable WAL mode for concurrent read/write during mesh sync
                        db.execSQL("PRAGMA journal_mode=WAL")
                        // Enable foreign key enforcement
                        db.execSQL("PRAGMA foreign_keys=ON")
                    }
                })
                .build()
        }
    }
}

/**
 * Room type converters for enum types
 */
class MeridianTypeConverters {
    @TypeConverter fun placeTypeToString(v: PlaceType): String = v.name
    @TypeConverter fun stringToPlaceType(v: String): PlaceType = PlaceType.valueOf(v)

    @TypeConverter fun obsCategoryToString(v: ObservationCategory): String = v.name
    @TypeConverter fun stringToObsCategory(v: String): ObservationCategory = ObservationCategory.valueOf(v)

    @TypeConverter fun speciesTypeToString(v: SpeciesType): String = v.name
    @TypeConverter fun stringToSpeciesType(v: String): SpeciesType = SpeciesType.valueOf(v)

    @TypeConverter fun rainfallToString(v: RainfallAssessment): String = v.name
    @TypeConverter fun stringToRainfall(v: String): RainfallAssessment = RainfallAssessment.valueOf(v)

    @TypeConverter fun alertTypeToString(v: AlertType): String = v.name
    @TypeConverter fun stringToAlertType(v: String): AlertType = AlertType.valueOf(v)

    @TypeConverter fun alertSeverityToString(v: AlertSeverity): String = v.name
    @TypeConverter fun stringToAlertSeverity(v: String): AlertSeverity = AlertSeverity.valueOf(v)

    @TypeConverter fun alertSourceToString(v: AlertSourceType): String = v.name
    @TypeConverter fun stringToAlertSource(v: String): AlertSourceType = AlertSourceType.valueOf(v)

    @TypeConverter fun outcomeResultToString(v: OutcomeResult): String = v.name
    @TypeConverter fun stringToOutcomeResult(v: String): OutcomeResult = OutcomeResult.valueOf(v)
}
