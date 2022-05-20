package com.myunidays.android

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myunidays.app.DashboardViewModel
import com.myunidays.app.RootCoordinator
import com.myunidays.app.StandardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RootCoordinatorView(navController: NavHostController = rememberNavController(), coordinator: Coordinator) {

    val dashboardViewModel: DashboardViewModel? by coordinator.dashboardViewModel.observeAsState()
    val standardViewModel: StandardViewModel? by coordinator.standardViewModel.observeAsState()
    val canGoBack: Boolean by coordinator.canGoBack.collectAsState(false)

    if (dashboardViewModel != null && standardViewModel == null) {
        navController.navigate("dashboardView")
    }
    if (standardViewModel != null) {
        navController.navigate("standardView")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    if (canGoBack) {
                        IconButton(onClick = {
                            navController.popBackStack()
                            coordinator.onBack()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "Back")
                        }
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "loadingView"
        ) {
            composable("loadingView") {
                Text("Loading...")
            }
            composable("dashboardView") {
                dashboardViewModel?.let {
                    DashboardView(viewModel = it)
                }
            }
            composable("standardView") {
                standardViewModel?.let {
                    StandardView()
                }
            }
        }
    }
}

class Coordinator(private val rootCoordinator: RootCoordinator) {
    val dashboardViewModel: MutableLiveData<DashboardViewModel?> = MutableLiveData(null)
    val standardViewModel: MutableLiveData<StandardViewModel?> = MutableLiveData(null)
    val canGoBack = rootCoordinator.canGoBack

    init {
        rootCoordinator.scope.launch {
            rootCoordinator.screenFlow.collect {
                rootCoordinator.scope.launch(Dispatchers.Main) {
                    when (it) {
                        is DashboardViewModel -> dashboardViewModel.value = it
                        is StandardViewModel -> standardViewModel.value = it
                    }
                }
            }
        }
    }

    fun onBack() {
        rootCoordinator.onBack()
    }
}