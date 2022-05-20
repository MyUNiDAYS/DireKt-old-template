//
//  DashboardView.swift
//  ios
//
//  Created by Andrew Reed on 20/05/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import library

struct DashboardView: View {
    
    let dashboardViewModel: DashboardViewModel
    
    var body: some View {
        Button(action: {
            dashboardViewModel.standardButtonPressed()
        }, label: {
            Text("Push Standard View")
        })
    }
}
