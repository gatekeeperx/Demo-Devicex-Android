package com.gatekeeperx.devicex.foodhub.di

import com.gatekeeperx.devicex.foodhub.analytics.AnalyticsService
import com.gatekeeperx.devicex.foodhub.analytics.LogcatAnalyticsService
import com.gatekeeperx.devicex.foodhub.startup.EnvironmentData
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Core application-level Hilt module.
 *
 * Provides:
 * - [CoroutineScope] scoped to the application process lifetime (SupervisorJob).
 * - [CoroutineDispatcher] for IO operations.
 * - [EnvironmentData] with SDK configuration values.
 * - [AnalyticsService] binding to [LogcatAnalyticsService].
 */
@Suppress("unused") // Hilt resolves all bindings via KSP annotation processing
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsService(impl: LogcatAnalyticsService): AnalyticsService

    companion object {

        /**
         * Application-lifetime coroutine scope.
         *
         * Uses [SupervisorJob] so that a failing child coroutine (e.g. a startup task)
         * never cancels the scope or sibling coroutines.
         *
         * Lifecycle: created once, never cancelled — tied to process lifetime.
         * Cost: one Job + one CoroutineScope object on the heap.
         */
        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Default)

        /**
         * [Dispatchers.IO] provided as a named binding so it can be replaced
         * in unit tests with a test dispatcher (e.g. UnconfinedTestDispatcher).
         */
        @Provides
        @IoDispatcher
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        /**
         * SDK configuration for the FoodHub demo app.
         *
         * In production, [EnvironmentData.apiKey] would come from a backend-provisioned
         * secret, a BuildConfig field populated from CI, or an encrypted config — never
         * hardcoded in the AAR. For the demo this is acceptable.
         */
        @Provides
        @Singleton
        fun provideEnvironmentData(): EnvironmentData = EnvironmentData(
            apiKey = "SMhdDIwkct5TJ5djY67xC2B7RfK4PP0j5e5fKkpa",
            tenant = "rappi",
            isSandbox = true,
        )
    }
}
