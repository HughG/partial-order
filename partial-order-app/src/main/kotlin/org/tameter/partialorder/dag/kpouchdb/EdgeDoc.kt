package org.tameter.partialorder.dag.kpouchdb

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface EdgeDoc : GraphElementDoc {
    val graphId: String
    val fromId: String
    val toId: String
}

fun EdgeDoc(_id: String, graphId: String, fromId: String, toId: String): EdgeDoc {
    return GraphElementDoc<EdgeDoc>(_id, "E").apply {
        this.asDynamic().graphId = graphId
        this.asDynamic().fromId = fromId
        this.asDynamic().toId = toId
    }
}

fun EdgeDoc(graph: String, from: String, to: String): EdgeDoc {
    return EdgeDoc("g_${graph}_f_${from}_t_${to}", graph, from, to)
}

fun EdgeDoc(doc: EdgeDoc): EdgeDoc {
    return EdgeDoc(doc.graphId, doc.fromId, doc.toId)
}