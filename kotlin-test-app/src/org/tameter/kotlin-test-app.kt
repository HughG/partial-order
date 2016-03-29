package org.tameter

import kotlin.browser.document

/**
 * Created by hughg on 2016-03-28.
 */

/*
    See very useful info about (newbie mistakes with) promises at

    https://pouchdb.com/2015/05/18/we-have-a-problem-with-promises.html
 */

// NOTE 2016-03-29 HughG: This doesn't work as an interface in compile-to-JS: catchAndLog isn't in the output.
@native
abstract class Promise<T> {
    fun then(result: (T) -> T): Promise<T>
    fun catch(error: (T) -> Unit): Unit
    fun catchAndLog(): Unit {
        catch { console.log(it) }
    }
}

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
    fun destroy(options: JSMap<dynamic> = JSMap()): Promise<dynamic> = noImpl

    // Create/update doc
    fun put(doc: dynamic): Promise<dynamic> = noImpl
    fun get(id: String): Promise<dynamic> = noImpl

    // Batch create
    fun bulkDocs(docs: Array<Any>, options: JSMap<dynamic> = JSMap()): Promise<dynamic> = noImpl

    // Batch fetch
    fun <T> allDocs(options: JSMap<dynamic> = JSMap()): Promise<BulkQueryResult<T>> = noImpl

    // Database info
    fun info(): Promise<dynamic> = noImpl
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
}

fun edge(_id: String): Edge {
    return initPouchDoc(Edge(), "Edge", _id)
}

@native("Object")
class Axis<E> : PouchDoc() {
//    var edges: Array<E> = noImpl
}

fun main(args: Array<String>) {
//    console.log("Setting email ...")
//    document.getElementById("email")?.setAttribute("value", "hello@kotlinlang.org")

    val (db, promise) = initDB()

    promise.then {
        proposeEdges(db)
    }.catchAndLog()
}

fun proposeEdges(db: PouchDB): Promise<dynamic> {
    // Read all nodes and edges
    val allNodesOptions = JSMap<dynamic>().apply {
        this["startkey"] = "Node_"
        this["endkey"] = "Node_\uffff"
        this["include_docs"] = true
    }
    console.log(allNodesOptions)
    return db.allDocs<Node>(allNodesOptions).then { result ->
        console.log(result)
        result.rows.forEach { console.log(it.doc?.description ?: "no desc") }
        result
    }

    // Find set of all possible edges
    // Remove existing edges
    // Return result
}

private val DB_NAME = "http://localhost:5984/ranking"

private fun initDB(): Pair<PouchDB, Promise<dynamic>> {
    val (db, promise) = resetDB()
    return Pair(db, promise.then { addDummyData(db) })
}

private fun resetDB(): Pair<PouchDB, Promise<dynamic>> {
    (PouchDB(DB_NAME)).destroy()
    val db: PouchDB = PouchDB(DB_NAME)

    val promise = db.info().then { console.log(it) }
    return Pair(db, promise)
}

private fun addDummyData(db: PouchDB): Promise<dynamic> {
    var readNode = node("read").apply {
        description = "Investigate stuff";
    }
    var sighNode = node("sigh").apply {
        description = "Be frustrated at difficulty of new stuff";
    }
    var grumpNode = node("grump").apply {
        description = "Grumble to self about difficulty of new stuff";
    }
    var edge1 = edge("edge1").apply {
        axis_id = "Dependency";
        from = readNode._id;
        to = sighNode._id;
    }
    //TODO 2016-03-29 HughG: Use bulkDocs?  This was an experiment in chaining.
    return db.put(readNode).then {
        db.put(sighNode)
    }.then {
        db.put(grumpNode)
    }.then {
        db.put(edge1)
    }
}
