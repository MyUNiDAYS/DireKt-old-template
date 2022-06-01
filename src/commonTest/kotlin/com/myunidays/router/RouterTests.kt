package com.myunidays.router

import app.cash.turbine.test
import com.myunidays.transition.Transition
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RouterTests {

    lateinit var router: Router<TestConfig, Any>

    @BeforeTest
    fun setup() {
        router = RouterImpl(
            TestConfig.One,
            ::createChild,
            ::configForName
        )
    }

    @Test
    fun testPushOfConfigWorks() = runTest {
        router.stack.test {
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
    fun testPopOfConfigWorks() = runTest {
        router.stack.test {
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
    fun testReplaceOfConfigWorks() = runTest {
        router.stack.test {
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
    fun testDeeplinks() = runTest {
        router.handleDeeplink("Direkt://Four?id=123&abc=hi")
        router.stack.test {
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

    private fun createChild(config: TestConfig): Any = when (config) {
        TestConfig.One -> TODO()
        TestConfig.Three -> TODO()
        TestConfig.Two -> TODO()
        is TestConfig.Four -> TODO()
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
