package org.tameter.kotlin.js

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@Suppress("NOTHING_TO_INLINE")
inline fun <T> jsobject(): T = js("{ return {}; }")

@Suppress("NOTHING_TO_INLINE")
inline fun <T> jsobject(init: T.() -> Unit): T = jsobject<T>().apply(init)
