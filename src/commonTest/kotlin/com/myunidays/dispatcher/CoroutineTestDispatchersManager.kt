package com.myunidays.dispatcher

import kotlinx.coroutines.test.TestDispatcher

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
expect class CoroutineTestDispatchersManager(testDispatcher: TestDispatcher) {
    val testDispatcher: TestDispatcher

    val dispatcherProvider: DispatcherProvider

    fun start()
    fun stop()
}
