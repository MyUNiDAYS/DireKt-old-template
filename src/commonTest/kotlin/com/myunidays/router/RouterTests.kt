package com.myunidays.router

import app.cash.turbine.test
import com.myunidays.transition.Transition
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RouterTests {

    lateinit var router: Router<TestConfig, Any>

    @BeforeTest
    fun setup() {
        router = RouterImpl<TestConfig, Any>(
            TestConfig.One,
            ::createChild
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

    private fun createChild(config: TestConfig): Any = when (config) {
        TestConfig.One -> TODO()
        TestConfig.Three -> TODO()
        TestConfig.Two -> TODO()
    }
}

sealed class TestConfig(key: String, params: Map<String, String>) : RoutingConfig(key, params) {
    object One : TestConfig("One", emptyMap())
    object Two : TestConfig("Two", emptyMap())
    object Three : TestConfig("Three", emptyMap())
}
