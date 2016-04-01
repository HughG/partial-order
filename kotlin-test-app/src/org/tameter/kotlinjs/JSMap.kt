package org.tameter.kotlinjs

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 *
 * Adapted from http://stackoverflow.com/a/28150862/6128163.
 */

@native("Object")
open class JSMap<T> {
    @nativeGetter
    operator fun get(key: String): T = noImpl

    @nativeSetter
    operator fun set(key: String, value: T): Unit = noImpl
}