package org.tameter.partialorder.dag

import org.tameter.kpouchdb.PouchDoc
import org.tameter.kpouchdb.initPouchDoc

@native("Object")
class Edge/*<N>*/ : PouchDoc() {
    var axis_id: String
    var from: String
    var to: String
}

fun Edge(from: Node, to: Node): Edge {
    return initPouchDoc(Edge(), "E", "f_${from._id}_t_${to._id}").apply {
        this.from = from._id
        this.to = to._id
    }
}