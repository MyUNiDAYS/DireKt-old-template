package com.myunidays.app

import com.myunidays.router.Router
import com.myunidays.router.RoutingConfig
import com.myunidays.transition.Transition
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

interface Routing<C: RoutingConfig> {
    val initialChild: C
    val router: Router<C, NavigateableInterface>
    fun createChild(config: C): NavigateableInterface
}

enum class PresentationStyle {
    Standard,
    Modal
}

interface NativeCoordinating {
    val presentationStyle: NativeFlow<PresentationStyle>
    val canGoBack: NativeFlow<Boolean>
    fun onBack()
}

abstract class Coordinator<C: RoutingConfig>(private val parentCoordinator: NativeCoordinating? = null): Routing<C>, NativeCoordinating {
    val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    protected val _screenFlow: MutableSharedFlow<NavigateableInterface> = MutableSharedFlow(1)
    val screenFlow: NativeFlow<NavigateableInterface> = _screenFlow.toNativeType(scope)

    override val presentationStyle: NativeFlow<PresentationStyle> = _screenFlow.map {
        if (router.canGoBack) PresentationStyle.Standard else PresentationStyle.Modal
    }.toNativeType(scope)

    protected fun initRouter() {
        scope.launch {
            router.stack.collect { (transition, config) ->
                var canGoBack = router.canGoBack
                if (transition == Transition.Push) {
                    router.createChild(config).let { navigateable ->
                        _screenFlow.emit(navigateable)
                        if (!navigateable.calculateCanGoBack()) {
                            canGoBack = false
                        }
                    }
                }
                _canGoBack.emit(canGoBack)
            }
        }
    }

    private fun NavigateableInterface.calculateCanGoBack() = this !is Coordinator<*>

    fun dispose() = scope.cancel("Disposing View Model Scope")

    override fun onBack() {
        scope.launch {
            if (router.canGoBack) {
                router.pop()
            } else {
                parentCoordinator?.onBack()
            }
            _screenFlow.replayCache.last().dispose()    // run dispose before we pop the view model
            _canGoBack.emit(router.canGoBack)
        }
    }

    // We can override this functionality if needed on the individual coordinators.
    private val _canGoBack: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val canGoBack: NativeFlow<Boolean> = _canGoBack.toNativeType(scope)
}

interface NavigateableInterface {
    val parentCoordinator: Coordinator<*>
    fun dispose()   // we need this so that on pop we can dispose.
}