package org.tameter

/**
 * Created by hughg on 2016-03-28.
 */

/*
    See very useful info about (newbie mistakes with) promises at

    https://pouchdb.com/2015/05/18/we-have-a-problem-with-promises.html

    TODO 2016-04-01 HughG: Add @CheckReturnValue from FundBugs / JSR 305
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
interface StoreResult {
    val ok: Boolean
    val id: String
    val rev: String
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
    fun put(doc: dynamic): Promise<StoreResult> = noImpl
    fun get(id: String): Promise<dynamic> = noImpl

    // Batch create
    fun bulkDocs(docs: Array<Any>, options: JSMap<dynamic> = JSMap()): Promise<Array<StoreResult>> = noImpl

    // Batch fetch
    fun <T> allDocs(options: JSMap<dynamic> = JSMap()): Promise<BulkQueryResult<T>> = noImpl

    // Database info
    fun info(): Promise<dynamic> = noImpl
}


@native("Object")
open class JSMap<T> {
    @nativeGetter
    operator fun get(key: String): T = noImpl

    @nativeSetter
    operator fun set(key: String, value: T): Unit = noImpl
}

open class Object : JSMap<dynamic>() {
}

fun jsobject(init: dynamic.() -> Unit): dynamic {
    return (Object()).apply(init)
}

@native("Object")
open class PouchDoc() : Object() {
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
    var description: String

}

fun node(_id: String): Node {
    return initPouchDoc(Node(), "N", _id)
}

@native("Object")
class Edge/*<N>*/ : PouchDoc() {
    var axis_id: String
    var from: String
    var to: String
}

fun edge(from: Node, to: Node): Edge {
    return initPouchDoc(Edge(), "E", "f_${from._id}_t_${to._id}").apply {
        this.from = from._id
        this.to = to._id
    }
}

@native("Object")
class Axis<E> : PouchDoc() {
//    var edges: Array<E> = noImpl
}

fun main(args: Array<String>) {
    initDB().thenP { db ->
        proposeEdges(db)
    }.catchAndLog()
}

fun proposeEdges(db: PouchDB): Promise<dynamic> {
    val allPossibleEdges: MutableCollection<Edge> = mutableListOf()

    // Read all nodes and edges
    return db.allDocs<Node>(jsobject {
        startkey = "N_"
        endkey = "N_\uffff"
        include_docs = true
    }).thenV {
        console.log(it)
        it.rows.forEach { console.log(it.doc?.description ?: "no desc") }
        it
    }.thenV {
        val nodes = it.rows.mapNotNull { it.doc }
        // Find set of all possible edges
        for (from in nodes) {
            for (to in nodes) {
                allPossibleEdges.add(edge(from, to))
            }
        }
        console.log("Possible Edges:")
        allPossibleEdges.forEach { console.log(it) }
        it
    }.thenP {
        // Remove existing edges
        db.allDocs<Edge>(jsobject {
            startkey = "E_"
            endkey = "E_\uffff"
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

        // Return result
        allPossibleEdges
    }
}

private val DB_NAME = "http://localhost:5984/ranking"

private fun initDB(): Promise<PouchDB> {
    return resetDB().thenV { db ->
        addDummyData(db)
        db
    }
}

private fun resetDB(): Promise<PouchDB> {
    return (PouchDB(DB_NAME)).destroy().thenP {
        val db: PouchDB = PouchDB(DB_NAME)

        db.info().thenV {
            console.log(it)
            db
        }
    }
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
    var edge1 = edge(readNode, sighNode).apply {
        axis_id = "Dependency";
    }
    return db.bulkDocs(arrayOf(
        readNode,
        sighNode,
        grumpNode,
        edge1
    )).thenV { results ->
        console.log("Bulk store results:")
        results.forEach { console.log(it) }
    }
}
