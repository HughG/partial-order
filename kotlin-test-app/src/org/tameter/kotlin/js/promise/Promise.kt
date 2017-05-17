package org.tameter.kotlin.js.promise

import org.tameter.kotlin.js.stack
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.js.Promise

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

/**
 * See very useful info about (newbie mistakes with) promises at
 *
 * https://pouchdb.com/2015/05/18/we-have-a-problem-with-promises.html
 */

/**
 * async and await here are from
 * https://discuss.kotlinlang.org/t/using-coroutines-to-avoid-callback-hell-when-using-xmlhttprequest/2450/3
 */
fun <T> async(c: suspend () -> T): Promise<T> {
    return Promise { resolve, reject ->
        c.startCoroutine(object : Continuation<T> {
            override fun resume(value: T) = resolve(value)

            override fun resumeWithException(exception: Throwable) = reject(exception)

            override val context = EmptyCoroutineContext
        })
    }
}

inline suspend fun <T> Promise<T>.await() = kotlin.coroutines.experimental.suspendCoroutine<T> { c ->
    then({ c.resume(it) }, { c.resumeWithException(it) })
}


fun <T> Promise<T>.catchAndLog(): Promise<Unit> {
    return catch { console.log(it.toString() + ": " + it.stack) }
}
