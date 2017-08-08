package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc
import org.tameter.kpouchdb.toStringForExternal

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
external interface NodeDoc : PouchDoc {
    val source: String
    val description: String
}

fun NodeDoc.toStringForExternal(): String {
    return "${this.unsafeCast<PouchDoc>().toStringForExternal()}, description: ${description}"
}

fun NodeDoc(source: String, description: String): NodeDoc {
    return PouchDoc<NodeDoc>(source, "N").apply {
        this.asDynamic().source = source
        this.asDynamic().description = description
    }
}

fun NodeDoc(doc: NodeDoc): NodeDoc {
    return NodeDoc(doc.source, doc.description)
}
