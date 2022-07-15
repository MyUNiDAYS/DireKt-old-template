package com.myunidays.router

import com.myunidays.dispatcher.DefaultDispatcherProvider
import com.myunidays.dispatcher.DispatcherProvider
import com.myunidays.transition.Transition
import io.ktor.http.Url
import io.ktor.util.toMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RouterImpl<Config : RoutingConfig, Child>(
    initial: Config,
    private val configForName: (name: String, params: Map<String, List<String>>) -> Config?,
    dispatcher: DispatcherProvider = DefaultDispatcherProvider()
) : Router<Config, Child>, RouterTransitions<Config, Child> {

    override val activeChild: Config? get() = _stack.lastOrNull()
    override val stack: MutableSharedFlow<Pair<Transition, Config>> = MutableSharedFlow(1)
    private val _stack: MutableList<Config> = mutableListOf()

    // Jacobs Idea: We could store the whole list of changes, so that we could replay in the case of a crash.
    private val _stackHistory: MutableStateFlow<List<Pair<Transition, Config>>> = MutableStateFlow(emptyList())
    override val stackHistory: StateFlow<List<Pair<Transition, Config>>> = _stackHistory

    override val canGoBack: Boolean get() = _stack.size > 1

    init {
        CoroutineScope(dispatcher.default()).launch {
            stack.collect { transitionConfig ->
                _stackHistory.emit(_stackHistory.value + transitionConfig)
            }
        }
        CoroutineScope(dispatcher.default()).launch {
            push(initial)
        }
    }

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

    override suspend fun update(config: Config) {
        _stack.add(config)
        stack.emit(Transition.Update to config)
    }

    // we could have a more generic function, router.supportsConfig(key)?
    // Deeplinking stuff
    override suspend fun handleDeeplink(deeplink: String): String? =
        runCatching {
            Url(deeplink).let { deeplinkUrl ->
                val parameters = deeplinkUrl.parameters.toMap()
                if (parameters[Transition.key]?.firstOrNull() == Transition.Pop.name) {
                    pop()
                    return deeplink
                }
                return@runCatching configForName(
                    deeplinkUrl.host,
                    parameters
                )?.let { config ->
                    when (config.transition) {
                        Transition.Push -> push(config)
                        Transition.Pop -> pop()
                        Transition.Replace -> replace(config)
                        Transition.Update -> update(config)
                    }
                    return deeplink
                }
            }
        }
            .onFailure { println("Failed to parse deeplink $deeplink $it") }
            .getOrNull()
}
