package org.tameter

import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.Node

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
    var readNode = Node(g, "Investigate stuff")
    var sighNode = Node(g, "Be frustrated at difficulty of new stuff")
    var grumpNode = Node(g, "Grumble to self about difficulty of new stuff")
    var us1Node = Node(g, "Understand Promises better")
    var edge1 = Edge(g, readNode, sighNode).apply {
        //axis_id = "Dependency";
    }
    var edge2 = Edge(g, sighNode, us1Node).apply {
        //axis_id = "Dependency";
    }
    return db.bulkDocs(arrayOf(
            readNode,
            sighNode,
            grumpNode,
            us1Node,
            edge1,
            edge2
    ).map { it.doc }.toTypedArray()).thenV { results ->
        console.log("Bulk store results:")
        results.forEach { console.log(it) }
        db
    }

    // TODO 2016-04-01 HughG: Should sanity-check for cycles in the graph.
}