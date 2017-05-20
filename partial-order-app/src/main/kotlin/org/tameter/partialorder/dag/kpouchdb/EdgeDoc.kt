package org.tameter.partialorder.dag.kpouchdb

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface EdgeDoc : GraphElementDoc {
    val fromId: String
    val toId: String
}

fun EdgeDoc(_id: String, fromId: String, toId: String): EdgeDoc {
    return GraphElementDoc<EdgeDoc>(_id, "E").apply {
        this.asDynamic().fromId = fromId
        this.asDynamic().toId = toId
    }
}

fun EdgeDoc(from: String, to: String): EdgeDoc {
    return EdgeDoc("f_${from}_t_${to}", from, to)
}

fun EdgeDoc(doc: EdgeDoc): EdgeDoc {
    return EdgeDoc(doc.fromId, doc.toId)
}