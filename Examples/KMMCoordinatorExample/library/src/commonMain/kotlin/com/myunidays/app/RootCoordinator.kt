package com.myunidays.app

import com.myunidays.router.RouterImpl
import com.myunidays.router.RoutingConfig
import kotlinx.coroutines.launch

class RootCoordinator: Coordinator<RootConfig>() {
    override val initialChild = RootConfig.Dashboard

    override val router = RouterImpl<RootConfig, NavigateableInterface>(
        initialChild,
        ::createChild
    )

    override fun createChild(config: RootConfig): NavigateableInterface = when (config) {
        RootConfig.Dashboard -> DashboardViewModel(this)
        RootConfig.Standard -> StandardViewModel(this)
    }

    init {
        initRouter()
    }

    fun pushStandard() {
        scope.launch {
            router.push(RootConfig.Standard)
        }
    }

}

sealed class RootConfig(key: String): RoutingConfig(key) {
    object Dashboard: RootConfig("Dashboard")
    object Standard: RootConfig("Standard")
}