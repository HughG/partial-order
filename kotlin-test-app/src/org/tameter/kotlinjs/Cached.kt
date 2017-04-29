package org.tameter.kotlinjs

import kotlin.reflect.KProperty

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

interface Cached<out T> : Lazy<T> {
    fun clear()
}

/**
 * An extension to delegate a read-only property of type [T] to an instance of [Cached].
 *
 * This extension allows to use instances of Cached for property delegation.  Unlike the [Lazy]
 * interface, an instance of this will be held explicitly in a separate field, so that the
 * [Cached.clear] method can be called on it:
 *
 *     private val cachedProperty: String = cached { initializer |
 *     val property: String by cachedProperty
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> Cached<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

// Not thread-safe, but this is only for JavaScript.
private class CachedImpl<out T>(private val getValue: () -> T) : Cached<T> {
    private var initialized = false
    private var _value: T? = null

    override val value: T
        get() {
            if (!initialized) {
                _value = getValue()
                initialized = true
            }
            // We don't do "_value!!" here because T might be something like "Int?"
            return _value as T
        }

    override fun isInitialized(): Boolean = initialized

    override fun clear() {
        initialized = false
        _value = null // To allow GC
    }

    override fun toString(): String =
            if (isInitialized()) value.toString() else "Not currently initialized"
}

fun <T> cached(initializer: () -> T): Cached<T> = CachedImpl(initializer)
