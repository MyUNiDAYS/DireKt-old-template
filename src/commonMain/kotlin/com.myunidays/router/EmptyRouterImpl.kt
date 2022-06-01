package com.myunidays.router

import com.myunidays.transition.Transition
import kotlinx.coroutines.flow.emptyFlow

// We can use this empty router for the 'native' views which are unbound by KMM - maybe could be used for testing too.
class EmptyRouterImpl<Config : RoutingConfig, Child> : Router<Config, Child> {
    override val stack = emptyFlow<Pair<Transition, Config>>()
    override val canGoBack = false

    override suspend fun push(config: Config) = throw EmptyRouterException()
    override suspend fun pop() = throw EmptyRouterException()
    override suspend fun replace(config: Config) = throw EmptyRouterException()
    override fun createChild(config: Config) = throw EmptyRouterException()
    override val activeChild: Config? = null

    override suspend fun handleDeeplink(deeplink: String) = throw EmptyRouterException()

    class EmptyRouterException : Exception("Cannot call method on Empty Router")
}
