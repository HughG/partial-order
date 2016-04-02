package org.tameter.partialorder.dag

import org.tameter.kotlinjs.JSMapDelegate
import org.tameter.kotlinjs.makeGuid
import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.dag.kpouchdb.toStringForNative

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

    override fun toPrettyString(): String {
        return "{${_id} ${description}}"
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

// Copy constructor (also copies the doc)
fun Node(graph: Graph, node: Node): Node {
    return Node(graph, NodeDoc(node.doc))
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
