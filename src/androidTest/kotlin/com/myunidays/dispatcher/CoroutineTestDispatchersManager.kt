package com.myunidays.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
actual class CoroutineTestDispatchersManager actual constructor(actual val testDispatcher: TestDispatcher) {

    // We can use a different dispatched when running tests for Android
    actual val dispatcherProvider = object : DispatcherProvider {
        override fun default(): CoroutineDispatcher = testDispatcher
        override fun main(): CoroutineDispatcher = testDispatcher
        override fun unconfined(): CoroutineDispatcher = testDispatcher
    }

    actual fun start() {
        Dispatchers.setMain(testDispatcher)
    }

    actual fun stop() {
        Dispatchers.resetMain()
    }
}
