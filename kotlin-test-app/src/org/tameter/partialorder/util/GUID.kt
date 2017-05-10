package org.tameter.partialorder.util

import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Math

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

const val GUID_TEMPLATE: String = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx"

/**
 * Weakly-random GUID, adapted from http://stackoverflow.com/a/8809472/6128163.
 */
fun makeGuid(): String {
    inline @Suppress("unused", "UnsafeCastFromDynamic")
    fun Number.toString(radix: Int): String = asDynamic().toString(radix)

    var d: Int = Math.floor(Date().getTime() + window.performance.now())
    val uuid = GUID_TEMPLATE.replace(Regex("[xy]"), { c: MatchResult ->
        val r = Math.floor(Math.abs(d + Math.random() * 16)).rem(16)
        d /= 16
        (if (c.value == "x") r else (r and 0x3 or 0x8)).toString(16)
    })
    return uuid
}