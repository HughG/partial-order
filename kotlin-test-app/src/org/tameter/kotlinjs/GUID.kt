package org.tameter.kotlinjs

import kotlin.browser.window

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

const val GUID_TEMPLATE: String = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx"

/**
 * Weakly-random GUID, adapted from http://stackoverflow.com/a/8809472/6128163.
 */
fun makeGuid(): String {
    @Suppress("unused")
    @native fun Number.toString(radix: Int): String = noImpl

    var d: Int = Date().getTime() + Math.floor(window.performance.now())
    var uuid = GUID_TEMPLATE.replace(Regex("[xy]"), { c: MatchResult ->
        var r = Math.floor(d + Math.random()*16).mod(16)
        d /= 16
        (if (c.value == "x") r else (r and 0x3 or 0x8)).toString(16)
    })
    return uuid
}