package com.myunidays.transition

import com.myunidays.router.RoutingConfig

enum class Transition {
    Push,
    Pop,
    Replace,
    Update;
    companion object {
        val key: String = "transition"
    }
}

internal fun List<RoutingConfig>.transition(previous: List<RoutingConfig>?): Transition = when {
    previous == null -> Transition.Push
    size > previous.size -> Transition.Push
    size < previous.size -> Transition.Pop
    else -> Transition.Replace
}
