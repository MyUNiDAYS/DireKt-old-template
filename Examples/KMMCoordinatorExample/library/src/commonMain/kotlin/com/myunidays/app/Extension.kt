package com.myunidays.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

internal fun <T> Flow<T>.toNativeType(scope: CoroutineScope): NativeFlow<T> = NativeFlow(this, scope)

class NativeFlow<T>(private val origin: Flow<T>, private val scope: CoroutineScope) : Flow<T> by origin {
    fun collect(block: (T) -> Unit): Closeable = Job().apply {
        onEach { block(it) }.launchIn(CoroutineScope(scope.coroutineContext) + this)
    }.let {
        object : Closeable {
            override fun close() {
                it.cancel()
            }
        }
    }
}

// we can then close a nativeflow if we ever want to
interface Closeable {
    fun close()
}
