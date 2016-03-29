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

@native
interface BulkQueryResult<T> {
    val total_rows: Int
    val offset: Int
    val rows: Array<BulkQueryRow<T>>
}

@native
interface BulkQueryRow<T> {
    val id: String
    val key: String
    val value: BulkQueryRowValue
    val doc: T?
}

@native
interface BulkQueryRowValue {
    val rev: String
    val deleted: Boolean?
}

@Suppress("unused")
@native
open class PouchDB(var name: String, var options: JSMap<dynamic> = JSMap())
{
    // Delete database
    fun destroy(options: JSMap<dynamic> = JSMap()): Promise0 = noImpl

    // Create/update doc
    fun put(doc: dynamic): Promise0 = noImpl
    fun get(id: String): Promise0 = noImpl

    // Batch fetch
    fun allDocs(options: JSMap<dynamic> = JSMap()): Promise0 = noImpl
    fun <T> allDocsEx(options: JSMap<dynamic> = JSMap()): Promise<BulkQueryResult<T>> = noImpl

    // Database info
    fun info(): Promise0 = noImpl
}

@native("Object")
abstract class JSObject {
}

@native("Object")
class JSMap<T> {
    @nativeGetter
    operator fun get(key: String): T = noImpl

    @nativeSetter
    operator fun set(key: String, value: T): Unit = noImpl
}

@native("Object")
abstract class PouchDoc() : JSObject() {
    var _id: String = noImpl
    var type: String = noImpl
}

fun <T : PouchDoc> initPouchDoc(doc: T, type: String, _id: String): T {
    return doc.apply {
        this.type = type
        this._id = "${type}_${_id}"
    }
}

@native("Object")
class Node : PouchDoc() {
    init {
        type = "Node"
    }
    var description: String

}

fun node(_id: String): Node {
    return initPouchDoc(Node(), "Node", _id)
}

@native("Object")
class Edge/*<N>*/ : PouchDoc() {
    init {
        type = "Edge"
    }
    var axis_id: String
    var from: String
    var to: String
//    var from: N
//    var to: N
}

fun edge(_id: String): Edge {
    return initPouchDoc(Edge(), "Edge", _id)
}

@native("Object")
class Axis<E> : PouchDoc() {
//    var edges: Array<E> = noImpl
}

//@native("Object")
//class MultiGraph<N, A : Axis<E>, E : Edge<N>>(
//) {
//    var db: PouchDB = noImpl
//    var nodes: Array<N> = noImpl
//    var edges: Array<E> = noImpl
////    var axes: JSMap<A> = noImpl
//
//    fun create(dbName: String, init: MultiGraph<N,A,E>.() -> Unit) {
//        db = PouchDB(dbName)
//
//    }
//}

fun main(args: Array<String>) {
//    console.log("Setting email ...")
//    document.getElementById("email")?.setAttribute("value", "hello@kotlinlang.org")

    val db: PouchDB = initDB()

    proposeEdges(db)

//    console.log("Initialised ${doc}")

//    db.get("mittens").then(fun(doc): dynamic {
//        console.log("Read")
//        console.log(doc)
//        // update their age
//        doc.age = 4
//        // put them back
//        console.log("Writing")
//        console.log(doc)
//        return db.put(doc)
//    }).then(fun(doc): dynamic {
//        // fetch mittens again
//        return db.get("mittens")
//    }).then(fun(doc): dynamic {
//        console.log("Read back")
//        console.log(doc);
//        return null;
//    }).catch(fun(err) {
//        console.log("Read back")
//        console.log(err);
//    });
}

fun proposeEdges(db: PouchDB) {
    // Read all nodes and edges
    val allNodesOptions = JSMap<dynamic>().apply {
        this["startkey"] = "Node_"
        this["endkey"] = "Node_\uffff"
        this["include_docs"] = true
    }
    console.log(allNodesOptions)
//    db.allDocs<dynamic>(allNodesOptions).then {
//        console.log(it)
//        it.rows.forEach { console.log(it.doc?.description ?: "no desc") }
//        it
//    }
    db.allDocs(allNodesOptions).then { row ->
        console.log(row)
//        row.rows.forEach { console.log(row.doc?.description ?: "no desc") }
//        row
    }

    // Find set of all possible edges
    // Remove existing edges
    // Return result
}

private val DB_NAME = "http://localhost:5984/ranking"

private fun initDB(): PouchDB {
    val db: PouchDB = resetDB()
    addDummyData(db)
    return db
}

private fun resetDB(): PouchDB {
    (PouchDB(DB_NAME)).destroy()
    val db: PouchDB = PouchDB(DB_NAME)

    db.info().then { console.log(it) }
    return db
}

private fun addDummyData(db: PouchDB) {
    var readNode = node("read").apply {
        description = "Investigate stuff";
    }
    db.put(readNode)
    var sighNode = node("sigh").apply {
        description = "Be frustrated at difficulty of new stuff";
    }
    db.put(sighNode)
    var grumpNode = node("grump").apply {
        description = "Grumble to self about difficulty of new stuff";
    }
    db.put(sighNode)
    var edge1 = edge("edge1").apply {
        axis_id = "Dependency";
        from = readNode._id;
        to = sighNode._id;
    }
    db.put(edge1)
}
