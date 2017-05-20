package org.tameter.ksandbox

import kotlin.browser.window
import kotlin.coroutines.experimental.*
import kotlin.js.Promise

// From https://youtrack.jetbrains.com/issue/KT-17067
private object JavaScriptContext : AbstractCoroutineContextElement(ContinuationInterceptor.Key), ContinuationInterceptor {
    override fun <T> interceptContinuation(continuation: Continuation<T>) = object : Continuation<T> {
        override val context = continuation.context

        override fun resume(value: T) {
            window.setTimeout({ continuation.resume(value); }, 0)
        }

        override fun resumeWithException(exception: Throwable) {
            window.setTimeout({ continuation.resumeWithException(exception) }, 0)
        }
    }
}

// Also from https://youtrack.jetbrains.com/issue/KT-17067
fun <T> immediateAsync(c: suspend () -> T) {
    c.startCoroutine(object : Continuation<T> {
        override fun resume(value: T) { }

        override fun resumeWithException(exception: Throwable) { throw exception }

        override val context = JavaScriptContext
    })
}

// From https://discuss.kotlinlang.org/t/using-coroutines-to-avoid-callback-hell-when-using-xmlhttprequest/2450/3
fun <T> promiseAsync(c: suspend () -> T): Promise<T> {
    return Promise { resolve, reject ->
        c.startCoroutine(object : Continuation<T> {
            override fun resume(value: T) = resolve(value)

            override fun resumeWithException(exception: Throwable) = reject(exception)

            override val context = EmptyCoroutineContext
        })
    }
}

// Also from https://discuss.kotlinlang.org/t/using-coroutines-to-avoid-callback-hell-when-using-xmlhttprequest/2450/3
// Should work with promiseAsync; maybe doesn't make sense to use it with immediateAsync, I'm not sure.
inline suspend fun <T> Promise<T>.await() = suspendCoroutine<T> { c ->
    then({
        console.log("Resolving with $it")
        c.resume(it)
    }, {
        console.log("Rejecting with $it")
        c.resumeWithException(it)
    })
}

suspend fun <T> promise(value: T): Promise<T> {
    return Promise.resolve(value)
}

fun main(args: Array<String>) {
    promiseAsync {
        console.log("1")
        val a = promise("aardwolf").await()
        console.log(a)
        console.log("2")
    }
}
