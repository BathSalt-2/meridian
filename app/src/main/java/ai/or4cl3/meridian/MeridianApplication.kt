package ai.or4cl3.meridian

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ai.or4cl3.meridian.ai.GemmaEngine
import javax.inject.Inject

/**
 * MeridianApplication
 *
 * Hilt entry point. Triggers Gemma 4 engine initialization on startup
 * so the model is warm before the first farmer interaction.
 */
@HiltAndroidApp
class MeridianApplication : Application() {

    @Inject lateinit var gemmaEngine: GemmaEngine

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        warmUpGemmaEngine()
    }

    /**
     * Begin model loading immediately on app start.
     * By the time the farmer completes onboarding or reaches the dashboard,
     * the engine should be initialized and ready for inference.
     */
    private fun warmUpGemmaEngine() {
        applicationScope.launch {
            gemmaEngine.initialize(GemmaEngine.ModelTier.E4B)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // If the system is low on memory, drop to E2B and free E4B resources
        applicationScope.launch {
            gemmaEngine.close()
            gemmaEngine.initialize(GemmaEngine.ModelTier.E2B)
        }
    }
}
