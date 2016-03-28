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

@native
interface PouchDB {
    @native fun info(): Promise = noImpl
}

@native("PouchDB")
class PDB(var name: String): PouchDB {}

@native val db: PouchDB

fun main(args: Array<String>) {
    console.log("Setting email ...")
    document.getElementById("email")?.setAttribute("value", "hello@kotlinlang.org")
    console.log("  ... done")

    js("var db = new PouchDB('http://localhost:5984/kittens')")
//    js("var db = new PouchDB('kittens')")

    val db2: PDB = PDB("http://localhost:5984/rats")

    db.info().then({ console.log(it) })
    db2.info().then({ console.log(it) })
}