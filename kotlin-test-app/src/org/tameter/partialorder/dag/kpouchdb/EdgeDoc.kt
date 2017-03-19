package org.tameter.partialorder.dag.kpouchdb

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class EdgeDoc(_id: String, var fromId: String, var toId: String) : GraphElementDoc(_id, "E")

fun EdgeDoc(from: String, to: String): EdgeDoc {
    return EdgeDoc("f_${from}_t_${to}", from, to)
}

fun EdgeDoc(doc: EdgeDoc): EdgeDoc {
    return EdgeDoc(doc.fromId, doc.toId)
}