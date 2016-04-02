package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.initPouchDoc

@native("Object")
class EdgeDoc() : GraphElementDoc() {
//    var axis_id: String
    var fromId: String
    var toId: String
}

fun EdgeDoc(from: String, to: String): EdgeDoc {
    return initPouchDoc(EdgeDoc(), "E", "f_${from}_t_${to}").apply {
        this.fromId = from
        this.toId = to
    }
}

fun EdgeDoc(doc: EdgeDoc): EdgeDoc {
    return EdgeDoc(doc.fromId, doc.toId)
}