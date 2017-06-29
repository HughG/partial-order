package org.tameter.partialorder.source

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.MultiGraph
import org.tameter.partialorder.dag.Node

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
class DummyDataSource : Source{
    override fun populate(db: PouchDB) : Promise<PouchDB> {
        // We could just create NodeDoc and EdgeDoc instances directly, but putting them in a Graph forces a check that
        // the edges join known nodes.
        val mg = MultiGraph()
        val g = Graph("test", mg)
        val readNode = Node("Investigate stuff")
        val sighNode = Node("Be frustrated at difficulty of new stuff")
        val grumpNode = Node("Grumble to self about difficulty of new stuff")
        val us1Node = Node("Understand Promises better")
        val edge1 = Edge(g, readNode, sighNode).apply {
            //axis_id = "Dependency";
        }
        val edge2 = Edge(g, sighNode, us1Node).apply {
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
        return db.bulkDocs(dummyGraphObjects).thenV { results ->
            console.log("Bulk store results:")
            results.forEach { console.log(it) }
            db
        }

        // TODO 2016-04-01 HughG: Should sanity-check for cycles in the graph.
    }
}