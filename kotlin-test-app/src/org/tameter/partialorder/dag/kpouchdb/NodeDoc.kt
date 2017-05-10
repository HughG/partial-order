package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc
import org.tameter.kpouchdb.toStringForExternal

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
external interface NodeDoc : GraphElementDoc {
    val description: String
}

fun NodeDoc.toStringForExternal(): String {
    return "${this.unsafeCast<PouchDoc>().toStringForExternal()}, description: ${description}"
}


fun NodeDoc(_id: String, description: String): NodeDoc {
    return GraphElementDoc<NodeDoc>(_id, "N").apply {
        this.asDynamic().description = description
    }
}

fun NodeDoc(doc: NodeDoc): NodeDoc {
    return NodeDoc(doc._id, doc.description)
}
