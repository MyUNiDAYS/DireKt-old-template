package com.myunidays.router

import app.cash.turbine.test
import com.myunidays.dispatcher.runTestOnTestDispatchers
import com.myunidays.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RouterTests {

    lateinit var router: Router<TestConfig, Any>

    private val timeout = 5000L
    private val coroutineContext = Dispatchers.Default

    @BeforeTest
    fun setup() {
        router = RouterImpl(
            TestConfig.One,
            ::configForName
        )
    }

    @Test
    fun testPushOfConfigWorks() = runTestOnTestDispatchers {
        router.stack.test(timeout) {
            awaitItem().let { (transition, config) ->
                assertEquals(TestConfig.One, config)
                assertEquals(Transition.Push, transition)
            }
            router.push(TestConfig.Two)
            awaitItem().let { (transition, config) ->
                assertEquals(TestConfig.Two, config)
                assertEquals(Transition.Push, transition)
            }
        }
    }

    @Test
    fun testPopOfConfigWorks() = runTestOnTestDispatchers {
        router.stack.test(timeout) {
            awaitItem()
            router.push(TestConfig.Two)
            awaitItem().let { (transition, config) ->
                assertEquals(TestConfig.Two, config)
                assertEquals(Transition.Push, transition)
            }
            router.pop()
            awaitItem().let { (transition, config) ->
                assertEquals(TestConfig.One, config)
                assertEquals(Transition.Pop, transition)
            }
        }
    }

    @Test
    fun testReplaceOfConfigWorks() = runTestOnTestDispatchers {
        router.stack.test(timeout) {
            awaitItem()
            router.push(TestConfig.Two)
            awaitItem().let { (transition, config) ->
                assertEquals(TestConfig.Two, config)
                assertEquals(Transition.Push, transition)
            }
            router.replace(TestConfig.Three)
            awaitItem().let { (transition, config) ->
                assertEquals(TestConfig.Three, config)
                assertEquals(Transition.Replace, transition)
            }
        }
    }

    @Test
    fun testDeeplinks() = runTestOnTestDispatchers {
        router.handleDeeplink("Direkt://Four?id=123&abc=hi")
        router.stack.test(timeout) {
            val firstConfig = awaitItem()
            assertTrue { firstConfig.second is TestConfig.Four }
            assertEquals("123", (firstConfig.second as TestConfig.Four).id)

            router.handleDeeplink("Direkt://Two?id=123&abc=hi")
            val secondConfig = awaitItem()
            assertTrue { secondConfig.second is TestConfig.Two }

            router.handleDeeplink("direkt://three?id=123")
            val thirdConfig = awaitItem()
            assertTrue { thirdConfig.second is TestConfig.Three }
        }
    }

    private fun configForName(name: String, params: Map<String, List<String>>): TestConfig? =
        when (name.lowercase()) {
            "one" -> TestConfig.One
            "two" -> TestConfig.Two
            "three" -> TestConfig.Three
            "four" -> TestConfig.Four(params["id"]!!.first())
            else -> null
        }
}

sealed class TestConfig(
    key: String,
) : RoutingConfig(key) {

    object One : TestConfig("One")
    object Two : TestConfig("Two")
    object Three : TestConfig("Three")
    class Four(val id: String) : TestConfig("Four")
}
