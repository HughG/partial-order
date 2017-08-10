package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc
import org.tameter.kpouchdb.toStringForExternal

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
external interface NodeDoc : PouchDoc {
    // TODO 2017-08-10 HughG: Need to document what these mean!
    val source: String
    val sourceId: String
    val sourceDescription: String
    val index: Long
    val description: String
}

fun NodeDoc.toStringForExternal(): String {
    return "${this.unsafeCast<PouchDoc>().toStringForExternal()}, description: ${description}"
}

fun NodeDoc(source: String, sourceId: String, sourceDescription: String, index: Long, description: String): NodeDoc {
    val id = "${source}/${index}"
    return PouchDoc<NodeDoc>(id, "N").apply {
        this.asDynamic().source = source
        this.asDynamic().sourceId = sourceId
        this.asDynamic().sourceDescription = sourceDescription
        this.asDynamic().index = index
        this.asDynamic().description = description
    }
}

fun NodeDoc(doc: NodeDoc): NodeDoc {
    return NodeDoc(doc.source, doc.sourceId, doc.sourceDescription, doc.index, doc.description)
}
