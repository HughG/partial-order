package org.tameter

import kotlin.browser.document

/**
 * Created by hughg on 2016-03-28.
 */

@native
interface Promise {
    fun then(result: (dynamic) -> dynamic): Promise
    fun catch(error: (dynamic) -> Unit): Unit
}

@Suppress("unused")
@native
class PouchDB(var name: String, var options: Map<String, dynamic>)
{
    constructor(name: String) : this(name, mapOf())
    fun info(): Promise = noImpl
}

fun main(args: Array<String>) {
//    console.log("Setting email ...")
//    document.getElementById("email")?.setAttribute("value", "hello@kotlinlang.org")

    val db: PouchDB = PouchDB("http://localhost:5984/rats")

    db.info().then({ console.log(it) })
}