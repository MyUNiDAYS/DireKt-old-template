package com.myunidays.router

internal interface RouterTransitions<Config : RoutingConfig, Child> {
    suspend fun push(config: Config)
    suspend fun pop()
    suspend fun replace(config: Config)
    suspend fun update(config: Config)
}
