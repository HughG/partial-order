package org.tameter.kotlinjs

import kotlin.browser.window

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

/**
 * Weakly-random GUID, adapted from http://stackoverflow.com/a/8809472/6128163.
 */
fun makeGuid(): String {
    @native fun Number.toString(radix: Int): String = noImpl
    var d = Date().getTime() + Math.floor(window.performance.now())
    var uuid = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(Regex("[xy]"), { c: MatchResult ->
        var r = Math.floor(d + Math.random()*16).mod(16)
        d = Math.floor(d/16)
        (if (c.value == "x") r else (r and 0x3 or 0x8)).toString(16)
    })
    return uuid
}