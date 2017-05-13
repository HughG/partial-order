package org.tameter.partialorder.lib.jquery

import org.tameter.kotlin.js.promise.await
import kotlin.js.Promise


/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */

inline fun <T> JQueryPromise<T>.toKotlin() = unsafeCast<Promise<T>>()

inline suspend fun <T> JQueryPromise<T>.await() = toKotlin().await()
