package com.gatekeeperx.devicex.foodhub.di

import javax.inject.Qualifier

/**
 * Qualifier for the application-scoped [kotlinx.coroutines.CoroutineScope].
 * This scope is tied to the lifetime of the Application process.
 * It uses a [kotlinx.coroutines.SupervisorJob] so that individual task failures
 * do not cancel the entire scope.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

/**
 * Qualifier for [kotlinx.coroutines.Dispatchers.IO] to be injected as
 * a [kotlinx.coroutines.CoroutineDispatcher]. Enables safe testing by
 * swapping the dispatcher in tests.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher
