package org.tameter.kotlin.delegates

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
private object UNINITIALIZED_VALUE

class SetOnce<R, T> : ReadWriteProperty<R, T> {
    private var isSet: Boolean = false
    private var value: Any? = UNINITIALIZED_VALUE

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value === UNINITIALIZED_VALUE) {
            throw NullPointerException("${property.name} uninitialized")
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        if (isSet) {
            throw IllegalStateException("${property.name} already initialized")
        }
        this.value = value
        isSet = true
    }
}

fun <R, T> setOnce(): SetOnce<R, T> = SetOnce()