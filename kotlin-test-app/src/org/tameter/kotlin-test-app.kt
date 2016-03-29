package org.tameter

import kotlin.browser.document

/**
 * Created by hughg on 2016-03-28.
 */

/*
    See very useful info about (newbie mistakes with) promises at

    https://pouchdb.com/2015/05/18/we-have-a-problem-with-promises.html
 */

@native
abstract class Promise<T> {
    @native("then") fun <U> thenV(result: (T) -> U): Promise<U>
    @native("then") fun <U> thenP(result: (T) -> Promise<U>): Promise<U>
    fun catch(error: (T) -> Unit): Unit
}

fun <T> Promise<T>.catchAndLog(): Unit {
    catch { console.log(it) }
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
open class JSObject {
}

fun jsobject(init: dynamic.() -> Unit): dynamic {
    val result = JSObject()
    return result.apply(init)
}

@native("Object")
class JSMap<T> {
    @nativeGetter
    operator fun get(key: String): T = noImpl

    @nativeSetter
    operator fun set(key: String, value: T): Unit = noImpl
}

@native("Object")
open class PouchDoc() : JSObject() {
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

    promise.thenP {
        proposeEdges(db)
    }.catchAndLog()
}

fun proposeEdges(db: PouchDB): Promise<dynamic> {
    val allPossibleEdges: MutableCollection<Edge> = mutableListOf<Edge>()

    // Read all nodes and edges
    return db.allDocs<Node>(jsobject {
        startkey = "Node_"
        endkey = "Node_\uffff"
        include_docs = true
    }).thenV {
//        console.log(it)
        it.rows.forEach { console.log(it.doc?.description ?: "no desc") }
        it
    }.thenV {
        val nodes = it.rows.mapNotNull { it.doc }
        // Find set of all possible edges
        for (from in nodes) {
            for (to in nodes) {
                allPossibleEdges.add(edge("from_${from._id}_to_${to._id}").apply {
                    this.from = from._id
                    this.to = to._id
                })
            }
        }
        console.log("Possible Edges:")
        allPossibleEdges.forEach { console.log(it) }
        it
    }.thenP {
        // Remove existing edges
        db.allDocs<Edge>(jsobject {
            startkey = "Edge_"
            endkey = "Edge_\uffff"
            include_docs = true
        })
    }.thenV {
        val edges = it.rows.mapNotNull { it.doc }
        console.log("Actual Edges:")
        edges.forEach { console.log(it) }
        allPossibleEdges.removeAll { possibleE ->
            edges.any { actualE ->
                actualE.from == possibleE.from &&
                        actualE.to == possibleE.to
            }
        }
        console.log("Remaining Edges:")
        allPossibleEdges.forEach { console.log(it) }
    }

    // Return result
}

private val DB_NAME = "http://localhost:5984/ranking"

private fun initDB(): Pair<PouchDB, Promise<dynamic>> {
    val (db, promise) = resetDB()
    return Pair(db, promise.thenP { addDummyData(db) })
}

private fun resetDB(): Pair<PouchDB, Promise<dynamic>> {
    (PouchDB(DB_NAME)).destroy()
    val db: PouchDB = PouchDB(DB_NAME)

    val promise = db.info().thenV { console.log(it) }
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
    return db.put(readNode).thenP {
        db.put(sighNode)
    }.thenP {
        db.put(grumpNode)
    }.thenP {
        db.put(edge1)
    }
}
