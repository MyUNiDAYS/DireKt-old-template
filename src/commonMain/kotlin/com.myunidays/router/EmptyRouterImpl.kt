package com.myunidays.router

import com.myunidays.transition.Transition
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// We can use this empty router for the 'native' views which are unbound by KMM - maybe could be used for testing too.
class EmptyRouterImpl<Config : RoutingConfig, Child> : Router<Config, Child> {
    override val stack = MutableSharedFlow<Pair<Transition, Config>>(0)
    override val stackHistory: StateFlow<List<Pair<Transition, Config>>> = MutableStateFlow(emptyList())
    override val canGoBack = false

    override val activeChild: Config? = null

    override suspend fun handleDeeplink(deeplink: String) = throw EmptyRouterException()

    class EmptyRouterException : Exception("Cannot call method on Empty Router")
}
