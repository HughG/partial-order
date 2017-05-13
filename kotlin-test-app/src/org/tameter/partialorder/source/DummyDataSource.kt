package org.tameter.partialorder.source

import org.tameter.kotlin.js.promise.async
import org.tameter.kotlin.js.promise.await
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.GraphEdge
import org.tameter.partialorder.dag.GraphNode
import kotlin.js.Promise

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
class DummyDataSource : Source{
    override fun populate(db: PouchDB) : Promise<PouchDB> = async {
        // We could just create NodeDoc and EdgeDoc instances directly, but putting them in a Graph forces a check that
        // the edges join known nodes.
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
        val dummyGraphObjects = arrayOf(
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
        val results = db.bulkDocs(dummyGraphObjects).await()
        console.log("Bulk store results:")
        results.forEach { console.log(it) }
        db

        // TODO 2016-04-01 HughG: Should sanity-check for cycles in the graph.
    }
}