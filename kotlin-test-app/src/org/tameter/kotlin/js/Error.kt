/*
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */

package org.tameter.kotlin.js

external class Error(
        message: String = definedExternally,
        fileName: String = definedExternally,
        lineNumber: String = definedExternally
) {
    var message: String
    var name: String
}

var Error.stack: String
    get() {
        val stack = this.asDynamic().stack
        return if (stack == undefined) { "(stack is undefined)" } else { stack }
    }
    set(value) {
        this.asDynamic().stack = value
    }