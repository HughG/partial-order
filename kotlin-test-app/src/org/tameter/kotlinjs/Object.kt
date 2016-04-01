package org.tameter.kotlinjs

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

open class Object : JSMap<dynamic>() {
}

fun jsobject(init: dynamic.() -> Unit): dynamic {
    return (Object()).apply(init)
}