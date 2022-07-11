package com.myunidays.router

import com.myunidays.transition.Transition

abstract class RoutingConfig(
    val key: String,
    val transition: Transition = Transition.Push
)
