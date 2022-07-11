package com.myunidays.router

import com.myunidays.transition.Transition
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

// The router is COMPLETELY decoupled from the project, its using Generics and doesnt need to know about view models etc.
// Consider how we could potentially handle deeplinks from here?? - using the key from Routing config.

interface Router<Config : RoutingConfig, Child> {
    val stack: SharedFlow<Pair<Transition, Config>>
    val stackHistory: StateFlow<List<Pair<Transition, Config>>>
    val activeChild: Config?
    val canGoBack: Boolean
    // deeplink stuff
    suspend fun handleDeeplink(deeplink: String): String?
}

