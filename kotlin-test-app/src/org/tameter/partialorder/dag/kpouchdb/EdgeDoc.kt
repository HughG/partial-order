package org.tameter.partialorder.dag

import org.tameter.kpouchdb.initPouchDoc

@native("Object")
class EdgeDoc() : GraphElementDoc() {
//    var axis_id: String
    var from: String
    var to: String
}

fun EdgeDoc(from: String, to: String): EdgeDoc {
    return initPouchDoc(EdgeDoc(), "E", "f_${from}_t_${to}").apply {
        this.from = from
        this.to = to
    }
}