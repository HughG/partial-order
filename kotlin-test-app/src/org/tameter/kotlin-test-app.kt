package org.tameter

import kotlin.browser.document

/**
 * Created by hughg on 2016-03-28.
 */

@native
interface Promise<T> {
//    fun then(result: (T) -> dynamic): dynamic
    fun then(result: (T) -> T): Promise<T>
    fun catch(error: (T) -> Unit): Unit
}

@native
interface Promise0 : Promise<dynamic> {}

@Suppress("unused")
@native
class PouchDB(var name: String, var options: Map<String, dynamic>)
{
    constructor(name: String) : this(name, mapOf())
    fun info(): Promise0 = noImpl
    fun put(doc: dynamic): Promise0 = noImpl
    fun get(id: String): Promise0 = noImpl
}

abstract class JSObject {
    companion object {
        protected fun <T> initObject(obj: T, init: T.() -> Unit): T {
            obj.init()
            return obj
        }
    }
}

abstract class PouchDoc() : JSObject() {
    var _id: String = noImpl
}

@native("Object")
class Kitten(
        var name: String = noImpl,
        var occupation: String = noImpl,
        var age: Int = noImpl,
        var hobbies: Array<String> = noImpl
) : PouchDoc() {
    companion object Factory {
        fun new(init: Kitten.() -> Unit): Kitten = initObject(Kitten(), init)
    }
}

fun main(args: Array<String>) {
//    console.log("Setting email ...")
//    document.getElementById("email")?.setAttribute("value", "hello@kotlinlang.org")

    val db: PouchDB = PouchDB("http://localhost:5984/rats")

    db.info().then({ console.log(it) })

    var doc = Kitten().apply {
        _id = "mittens";
        name = "Mittens";
        occupation = "kitten";
        age = 3;
        hobbies = arrayOf(
                "playing with balls of yarn",
                "chasing laser pointers",
                "lookin' hella cute"
        );
    }

    console.log(doc)
    db.put(doc)

    db.get("mittens").then(fun(doc): dynamic {
        // update their age
        doc.age = 4
        // put them back
        return db.put(doc)
    }).then(fun(doc): dynamic {
        // fetch mittens again
        return db.get("mittens")
    }).then(fun(doc): dynamic {
        console.log(doc);
        return null;
    });
}