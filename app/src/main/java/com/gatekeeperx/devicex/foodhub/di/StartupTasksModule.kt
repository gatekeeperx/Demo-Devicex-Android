package com.gatekeeperx.devicex.foodhub.di

import com.gatekeeperx.devicex.foodhub.startup.MainApplicationComparator
import com.gatekeeperx.devicex.foodhub.startup.StartAppTask
import com.gatekeeperx.devicex.foodhub.startup.tasks.FirebaseStartAppTask
import com.gatekeeperx.devicex.foodhub.startup.tasks.GatekeeperXStartAppTask
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/**
 * Hilt module that assembles the startup task pipeline.
 *
 * ## How the multibinding works
 * Each [StartAppTask] implementation is contributed to a Hilt Set multibinding
 * via [@IntoSet][dagger.multibindings.IntoSet]. Hilt then injects the entire
 * `Set<StartAppTask>` into [com.gatekeeperx.devicex.foodhub.startup.MainApplicationInitializer].
 *
 * To add a new startup task:
 * 1. Implement [StartAppTask].
 * 2. Add a `@Binds @IntoSet` method below.
 * 3. Optionally add the class to [provideStartupTaskPriorityOrder] for explicit ordering.
 *
 * ## Priority order
 * The list returned by [provideStartupTaskPriorityOrder] defines which tasks are
 * dispatched first. Tasks absent from the list run last (lowest priority).
 *
 * Current order:
 *  1. [FirebaseStartAppTask]   — crash reporting must be up before any other SDK can fail
 *  2. [GatekeeperXStartAppTask] — device fingerprinting; critical for fraud detection
 */
@Suppress("unused") // Hilt resolves all bindings via KSP annotation processing
@Module
@InstallIn(SingletonComponent::class)
abstract class StartupTasksModule {

    // ─── Task bindings ────────────────────────────────────────────────────────

    @Binds
    @IntoSet
    @Singleton
    abstract fun bindFirebaseTask(impl: FirebaseStartAppTask): StartAppTask

    @Binds
    @IntoSet
    @Singleton
    abstract fun bindGatekeeperXTask(impl: GatekeeperXStartAppTask): StartAppTask

    companion object {

        /**
         * Provides the priority list consumed by [MainApplicationComparator].
         *
         * The list is ordered from highest priority (index 0) to lowest.
         * Tasks NOT present in this list are assigned lowest priority and
         * dispatched after all explicitly ordered tasks.
         *
         * Cost: a single immutable [List] allocation at startup.
         */
        @Provides
        @Singleton
        fun provideStartupTaskPriorityOrder(): List<@JvmSuppressWildcards Class<out StartAppTask>> = listOf(
            FirebaseStartAppTask::class.java,    // index 0 — highest priority
            GatekeeperXStartAppTask::class.java, // index 1
        )

        /**
         * Provides the [MainApplicationComparator] using the priority list above.
         * Singleton — created once, held in the application component.
         */
        @Provides
        @Singleton
        fun provideStartupTaskComparator(
            priorityOrder: List<@JvmSuppressWildcards Class<out StartAppTask>>,
        ): Comparator<StartAppTask> = MainApplicationComparator(priorityOrder)
    }
}
