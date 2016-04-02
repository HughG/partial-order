package org.tameter.partialorder.dag

import org.tameter.kotlinjs.makeGuid
import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@native("Object")
class Node(
        graph: Graph,
        doc: NodeDoc
) : GraphElement<NodeDoc>(graph, doc) {
    var description: String by doc

    fun outgoing(): Set<Edge> {
        return graph.edges.filter { it.from == this }.toSet()
    }
}

fun Node(
        graph: Graph,
        db: PouchDB,
        description: String
): Promise<Node> {
    val doc = NodeDoc(makeGuid()).apply {
        this.description = description
    }
    return db.put(doc).thenV { result ->
        if (!result.ok) {
            throw Exception("Failed to store ${doc}")
        }
        // Update rev to match DB, otherwise we won't be able to store any changes later.
        doc.rev = result.rev
        Node(graph, doc)
    }
}


