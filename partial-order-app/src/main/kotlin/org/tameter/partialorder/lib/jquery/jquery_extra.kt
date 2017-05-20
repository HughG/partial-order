package org.tameter.partialorder.lib.jquery

import org.tameter.kotlin.js.promise.Promise

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */

fun <T> JQueryPromise<T>.toPouchDB() = unsafeCast<Promise<T>>()
fun <T> Promise<T>.toJQuery() = unsafeCast<JQueryPromise<T>>()
