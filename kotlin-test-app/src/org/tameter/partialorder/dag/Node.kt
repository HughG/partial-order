package org.tameter.partialorder.dag

import org.tameter.kpouchdb.initPouchDoc

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@native("Object")
class Node(graph: Graph) : GraphElement(graph) {
    var description: String
    fun outgoing(): Set<Edge> {
        return graph.edges.filter { it.from._id == _id }.toSet()
    }
}

fun Node(graph: Graph, _id: String): Node {
    return initPouchDoc(Node(graph), "N", _id)
}