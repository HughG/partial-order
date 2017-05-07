package org.tameter

import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.GraphEdge
import org.tameter.partialorder.dag.GraphNode

private val DB_NAME = "http://localhost:5984/ranking"

internal fun initDB(): Promise<PouchDB> {
    return resetDB().thenP { db ->
        addDummyData(db)
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

private fun addDummyData(db: PouchDB): Promise<PouchDB> {
    val g: Graph = Graph()
    val readNode = GraphNode(g, "Investigate stuff")
    val sighNode = GraphNode(g, "Be frustrated at difficulty of new stuff")
    val grumpNode = GraphNode(g, "Grumble to self about difficulty of new stuff")
    val us1Node = GraphNode(g, "Understand Promises better")
    val edge1 = GraphEdge(g, readNode, sighNode).apply {
        //axis_id = "Dependency";
    }
    val edge2 = GraphEdge(g, sighNode, us1Node).apply {
        //axis_id = "Dependency";
    }
    val dummyGraphObjects: Array<Any> = arrayOf(
            readNode,
            sighNode,
            grumpNode,
            us1Node,
            edge1,
            edge2
    ).map { it.doc }.toTypedArray()
    console.log("Bulk store inputs:")
    dummyGraphObjects.forEach {
        console.log(it)
    }
    return db.bulkDocs(dummyGraphObjects).thenV { results ->
        console.log("Bulk store results:")
        results.forEach { console.log(it) }
        db
    }

    // TODO 2016-04-01 HughG: Should sanity-check for cycles in the graph.
}