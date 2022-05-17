package com.myunidays.router

import com.myunidays.transition.Transition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

class RouterImpl<Config : RoutingConfig, Child>(
    initial: Config,
    private val childFactory: (configuration: Config) -> Child
) : Router<Config, Child> {

    override val activeChild: Config get() = stack.value.second
    override val stack: MutableStateFlow<Pair<Transition, Config>> = MutableStateFlow(Transition.Push to initial)
    private val _stack: MutableList<Config> = mutableListOf(initial)

    // Jacobs Idea: We could store the whole list of changes, so that we could replay in the case of a crash.
    private val stackHistory: MutableList<Pair<Transition, Config>> = mutableListOf()

    override val canGoBack: Boolean get() = _stack.size > 1

    override suspend fun push(config: Config) {
        _stack.add(config)
        stack.emit(Transition.Push to config)
    }

    override suspend fun pop() {
        _stack.removeLast()
        stack.emit(Transition.Pop to _stack.last())
    }

    override suspend fun replace(config: Config) {
        _stack.removeLast()
        _stack.add(config)
        stack.emit(Transition.Replace to config)
    }

    override fun createChild(config: Config) = childFactory(config)

    // Deeplinking stuff
    override fun canHandleDeeplink(deeplink: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun handleDeeplink(deeplink: String) {
        TODO("Not yet implemented")
    }
}
