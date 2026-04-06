package ai.or4cl3.meridian.data.preferences

import ai.or4cl3.meridian.model.CommunityProfile
import ai.or4cl3.meridian.model.LaborCapacity
import ai.or4cl3.meridian.model.ModelTier
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "meridian_prefs")

/**
 * CommunityPreferences
 *
 * Manages the community profile and application preferences via DataStore.
 * Sensitive fields (community PIN hash) are stored separately in the
 * Android Keystore — not in DataStore.
 */
@Singleton
class CommunityPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object Keys {
        val COMMUNITY_ID = stringPreferencesKey("community_id")
        val COMMUNITY_NAME = stringPreferencesKey("community_name")
        val PRIMARY_LANGUAGE = stringPreferencesKey("primary_language")
        val REGION = stringPreferencesKey("region")
        val DEVICE_ID = stringPreferencesKey("device_id")
        val IS_PRIMARY_DEVICE = booleanPreferencesKey("is_primary_device")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val ONBOARDING_STEP = intPreferencesKey("onboarding_step")
        val MODEL_TIER = stringPreferencesKey("model_tier")
        val MESH_ENABLED = booleanPreferencesKey("mesh_enabled")
        val PSALM_MESH_ENABLED = booleanPreferencesKey("psalm_mesh_enabled")
        val CREATED_AT = longPreferencesKey("created_at")
    }

    val communityProfile: Flow<CommunityProfile?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            val communityId = prefs[Keys.COMMUNITY_ID] ?: return@map null
            CommunityProfile(
                communityId = communityId,
                communityName = prefs[Keys.COMMUNITY_NAME] ?: "",
                primaryLanguage = prefs[Keys.PRIMARY_LANGUAGE] ?: "en",
                region = prefs[Keys.REGION] ?: "",
                deviceId = prefs[Keys.DEVICE_ID] ?: UUID.randomUUID().toString(),
                isPrimaryDevice = prefs[Keys.IS_PRIMARY_DEVICE] ?: false,
                onboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false,
                onboardingStep = prefs[Keys.ONBOARDING_STEP] ?: 0,
                preferredModel = ModelTier.valueOf(prefs[Keys.MODEL_TIER] ?: ModelTier.E4B.name),
                meshEnabled = prefs[Keys.MESH_ENABLED] ?: false,
                psalMeshEnabled = prefs[Keys.PSALM_MESH_ENABLED] ?: false,
                createdAt = prefs[Keys.CREATED_AT] ?: System.currentTimeMillis()
            )
        }

    val isOnboardingComplete: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[Keys.ONBOARDING_COMPLETE] ?: false }

    suspend fun saveCommunityProfile(profile: CommunityProfile) {
        dataStore.edit { prefs ->
            prefs[Keys.COMMUNITY_ID] = profile.communityId
            prefs[Keys.COMMUNITY_NAME] = profile.communityName
            prefs[Keys.PRIMARY_LANGUAGE] = profile.primaryLanguage
            prefs[Keys.REGION] = profile.region
            prefs[Keys.DEVICE_ID] = profile.deviceId
            prefs[Keys.IS_PRIMARY_DEVICE] = profile.isPrimaryDevice
            prefs[Keys.ONBOARDING_COMPLETE] = profile.onboardingComplete
            prefs[Keys.ONBOARDING_STEP] = profile.onboardingStep
            prefs[Keys.MODEL_TIER] = profile.preferredModel.name
            prefs[Keys.MESH_ENABLED] = profile.meshEnabled
            prefs[Keys.PSALM_MESH_ENABLED] = profile.psalMeshEnabled
            prefs[Keys.CREATED_AT] = profile.createdAt
        }
    }

    suspend fun updateOnboardingStep(step: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_STEP] = step
            if (step >= 3) prefs[Keys.ONBOARDING_COMPLETE] = true
        }
    }

    suspend fun setMeshEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.MESH_ENABLED] = enabled }
    }
}
