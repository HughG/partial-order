package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc
import org.tameter.kpouchdb.toStringForExternal

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
external interface NodeDoc : GraphElementDoc {
    var description: String
}

fun NodeDoc.toStringForExternal(): String {
    @Suppress("UNCHECKED_CAST_TO_NATIVE_INTERFACE")
    return "${(this as PouchDoc).toStringForExternal()}, description: ${description}"
}


fun NodeDoc(_id: String, description: String): NodeDoc {
    return GraphElementDoc<NodeDoc>(_id, "N").apply {
        this.description = description
    }
}

fun NodeDoc(doc: NodeDoc): NodeDoc {
    return NodeDoc(doc._id, doc.description)
}
