package org.tameter.kotlinjs

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@Suppress("NOTHING_TO_INLINE")
inline fun <T> jsobject(): T = js("{ return {}; }")
