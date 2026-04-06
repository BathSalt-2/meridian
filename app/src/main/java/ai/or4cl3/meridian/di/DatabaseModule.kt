package ai.or4cl3.meridian.di

import ai.or4cl3.meridian.data.dao.*
import ai.or4cl3.meridian.data.db.MeridianDatabase
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMeridianDatabase(@ApplicationContext context: Context): MeridianDatabase =
        MeridianDatabase.getInstance(context)

    @Provides fun providePlaceDao(db: MeridianDatabase): PlaceDao = db.placeDao()
    @Provides fun provideObservationDao(db: MeridianDatabase): ObservationDao = db.observationDao()
    @Provides fun provideAlertDao(db: MeridianDatabase): AlertDao = db.alertDao()
    @Provides fun provideInterventionDao(db: MeridianDatabase): InterventionDao = db.interventionDao()

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
