/*
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */

package org.tameter.kotlin.js

import kotlin.js.*

var Error.stack: String
    get() {
        val stack = this.asDynamic().stack
        return if (stack == undefined) { "(stack is undefined)" } else { stack }
    }
    set(value) {
        this.asDynamic().stack = value
    }

fun logError(err: Error) {
    val errJson = err.unsafeCast<Json>()
    val errObjectString = err.keys()
            .map { "${it}: ${errJson[it]}" }
            .joinToString(prefix = "[", separator = ", ", postfix = "]")
    console.log("Caught $errObjectString")
    console.log("${err.message}: ${err.stack}")
}