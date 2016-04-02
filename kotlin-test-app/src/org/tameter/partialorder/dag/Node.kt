package org.tameter.partialorder.dag

import org.tameter.kotlinjs.JSMapDelegate
import org.tameter.kotlinjs.makeGuid
import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class Node(
        graph: Graph,
        doc: NodeDoc
) : GraphElement<NodeDoc>(graph, doc) {
    var description: String by JSMapDelegate(doc)

    init {
        graph.nodes.add(this)
    }

    fun outgoing(): Set<Edge> {
        return graph.edges.filter { it.from == this }.toSet()
    }

    override fun toString(): String{
        return "Node(dscr ${description}, doc ${doc.toStringForNative()})"
    }
}

fun Node(
        graph: Graph,
        description: String
): Node {
    val doc = NodeDoc(makeGuid()).apply {
        this.description = description
    }
    val node = Node(graph, doc)
//    console.log(node.toString())
    return node
}

fun Node.store(
        db: PouchDB
): Promise<Node> {
    return db.put(doc).thenV { result ->
        if (!result.ok) {
            throw Exception("Failed to store ${doc}")
        }
        // Update rev to match DB, otherwise we won't be able to store any changes later.
        doc.rev = result.rev
        this
    }
}
