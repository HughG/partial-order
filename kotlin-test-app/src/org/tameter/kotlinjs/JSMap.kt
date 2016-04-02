package org.tameter.kotlinjs

import kotlin.reflect.KProperty

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

/**
 * These methods can't be on JSMap directly otherwise they're not emitted into the JavaScript,
 * because that's a @native class.
 */
class JSMapDelegate<T>(
        val map: JSMap<T>
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return map[property.name]
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        map[property.name] = value
    }

}