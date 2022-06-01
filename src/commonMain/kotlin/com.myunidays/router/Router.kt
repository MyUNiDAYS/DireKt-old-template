package com.myunidays.router

import com.myunidays.transition.Transition
import kotlinx.coroutines.flow.Flow

// The router is COMPLETELY decoupled from the project, its using Generics and doesnt need to know about view models etc.
// Consider how we could potentially handle deeplinks from here?? - using the key from Routing config.

interface Router<Config : RoutingConfig, Child> {
    val stack: Flow<Pair<Transition, Config>>
    val activeChild: Config?
    val canGoBack: Boolean

    suspend fun push(config: Config)
    suspend fun pop()
    suspend fun replace(config: Config)

    fun createChild(config: Config): Child

    // deeplink stuff
    suspend fun handleDeeplink(deeplink: String): String
}
