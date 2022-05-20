package com.myunidays.app

class DashboardViewModel(override val parentCoordinator: RootCoordinator) : NavigateableInterface {

    override fun dispose() {}

    fun standardButtonPressed() {
        parentCoordinator.pushStandard()
    }

}