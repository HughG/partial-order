/*
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */

package org.tameter.kotlin.js

var Throwable.stack: String
    get() {
        val stack = this.asDynamic().stack
        return if (stack == undefined) { "(stack is undefined)" } else { stack }
    }
    set(value) {
        this.asDynamic().stack = value
    }