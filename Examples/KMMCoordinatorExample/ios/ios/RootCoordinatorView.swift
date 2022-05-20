//
//  RootCoordinatorView.swift
//  ios
//
//  Created by Andrew Reed on 20/05/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import library

struct RootCoordinatorView: View {
    
    @ObservedObject var coordinator: Coordinator
    
    var body: some View {
        NavigationView {
            if ($coordinator.dashboardViewModel.wrappedValue != nil) {
                DashboardView(dashboardViewModel: $coordinator.dashboardViewModel.wrappedValue!)
                    .navigation(item: $coordinator.standardViewModel) { _ in
                        StandardView()
                    }
            } else {
                Text("Loading...")
            }
        }
    }
}

class Coordinator: ObservableObject {
    @Published var dashboardViewModel: DashboardViewModel?
    @Published var standardViewModel: StandardViewModel?
    
    init(rootCoordinator: RootCoordinator) {
        rootCoordinator.screenFlow.collect { viewModel in
            DispatchQueue.main.async {
                if let viewModel = viewModel as? DashboardViewModel {
                    self.dashboardViewModel = viewModel
                }
                if let viewModel = viewModel as? StandardViewModel {
                    self.standardViewModel = viewModel
                }
            }
        }
    }
}
    
