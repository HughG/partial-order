package org.tameter.kotlin.collections

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
class MutableMapWithDefault<K, V>(
        private val m: MutableMap<K, V>,
        private val defaultValue: () -> V
) : MutableMap<K, V> by m {
    override fun get(key: K): V = m.getOrPut(key, defaultValue)
}

fun <K, V> MutableMap<K, V>.withDefaultValue(defaultValue: () -> V) =
        MutableMapWithDefault(this, defaultValue)
