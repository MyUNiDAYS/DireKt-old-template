package com.myunidays.router

import com.myunidays.dispatcher.DefaultDispatcherProvider
import com.myunidays.dispatcher.DispatcherProvider
import com.myunidays.transition.Transition
import io.ktor.http.Url
import io.ktor.util.toMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class RouterImpl<Config : RoutingConfig, Child>(
    initial: Config,
    private val configForName: (name: String, params: Map<String, List<String>>) -> Config?,
    dispatcher: DispatcherProvider = DefaultDispatcherProvider()
) : Router<Config, Child> {

    override val activeChild: Config? get() = _stack.lastOrNull()
    override val stack: MutableSharedFlow<Pair<Transition, Config>> = MutableSharedFlow(1)
    private val _stack: MutableList<Config> = mutableListOf()

    // Jacobs Idea: We could store the whole list of changes, so that we could replay in the case of a crash.
    private val stackHistory: MutableList<Pair<Transition, Config>> = mutableListOf()

    override val canGoBack: Boolean get() = _stack.size > 1

    init {
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

    // we could have a more generic function, router.supportsConfig(key)?
    // Deeplinking stuff
    override suspend fun handleDeeplink(deeplink: String): String? =
        runCatching {
            Url(deeplink).let { deeplinkUrl ->
                return@runCatching configForName(
                    deeplinkUrl.host,
                    deeplinkUrl.parameters.toMap()
                )?.let { config ->
                    push(config)
                    return deeplink
                }
            }
        }
            .onFailure { println("Failed to parse deeplink $deeplink $it") }
            .getOrNull()
}
