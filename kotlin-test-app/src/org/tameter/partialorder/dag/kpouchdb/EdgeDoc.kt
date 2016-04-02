package org.tameter.partialorder.dag

import org.tameter.kpouchdb.initPouchDoc

@native("Object")
class Edge(graph: Graph) : GraphElement(graph) {
//    var axis_id: String
    var from: Node
    var to: Node
}

fun Edge(graph: Graph, from: Node, to: Node): Edge {
    return initPouchDoc(Edge(graph), "E", "f_${from._id}_t_${to._id}").apply {
        this.from = from
        this.to = to
    }
}