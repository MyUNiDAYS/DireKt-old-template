package com.myunidays.dispatcher

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
fun runTestOnTestDispatchers(
    testBody: suspend TestScope.(dispatcher: DispatcherProvider) -> Unit
): TestResult {

    // Setup test scheduler to allow for delay skipping
    val scheduler = TestCoroutineScheduler()
    /*
    Setup test dispatcher using our test scheduler.
    UnconfiedTestDispatcher does not guarantee the order coroutines are launched
    but it launches them eagerly without us having to manually progress time
    */
    val testDispatcher = UnconfinedTestDispatcher(scheduler)

    val coroutineTestDispatchersManager = CoroutineTestDispatchersManager(testDispatcher)
    // Replace the main dispatcher with our test dispatcher
    coroutineTestDispatchersManager.start()

    return runTest(testDispatcher) {
        // Run the test
        testBody(coroutineTestDispatchersManager.dispatcherProvider)
        // Reset the main dispatcher
        coroutineTestDispatchersManager.stop()
    }
}
