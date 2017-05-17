package org.tameter.partialorder.lib.jquery

import kotlin.js.Promise


/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */

inline fun <T> JQueryPromise<T>.toKotlin() = unsafeCast<Promise<T>>()

//inline suspend fun <T> JQueryPromise<T>.await() = toKotlin().await()


inline suspend fun <T> JQueryPromise<T>.await() = kotlin.coroutines.experimental.suspendCoroutine<T> { c ->
    then({ value: T?, _: Any -> c.resume(value!!) }, { c.resumeWithException(it as Throwable) })
}

