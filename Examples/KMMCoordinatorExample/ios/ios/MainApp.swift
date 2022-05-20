//
//  MainApp.swift
//  ios
//
//  Created by Andrew Reed on 20/05/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import library

@main
struct MainApp: App {
    
    //@StateObject var coordinator = TabsCoordinator()
    
    var body: some Scene {
        WindowGroup {
            RootCoordinatorView(coordinator: Coordinator(rootCoordinator: MainKt.initialize()))
        }
    }
}
