package org.tameter

import kotlin.browser.document

/**
 * Created by hughg on 2016-03-28.
 */

fun main(args: Array<String>) {
    console.log("Setting email ...")
    document.getElementById("email")?.setAttribute("value", "hello@kotlinlang.org")
    console.log("  ... done")
}