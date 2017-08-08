package org.tameter.kotlin.js

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@Suppress("NOTHING_TO_INLINE")
inline fun <T> jsobject(): T = js("{ return {}; }").unsafeCast<T>()

@Suppress("NOTHING_TO_INLINE")
inline fun <T> jsobject(init: T.() -> Unit): T = jsobject<T>().apply(init)

@Suppress("NOTHING_TO_INLINE")
inline val Any.objectKeys: Array<String> get() = js("Object").keys(asDynamic())