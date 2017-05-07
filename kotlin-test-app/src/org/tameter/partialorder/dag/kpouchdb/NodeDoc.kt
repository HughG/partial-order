package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc
import org.tameter.kpouchdb.toStringForExternal

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
external class NodeDoc(description: String) : GraphElementDoc {
    var description: String
}

fun NodeDoc.toStringForExternal(): String {
    @Suppress("UNCHECKED_CAST_TO_NATIVE_INTERFACE")
    return "${(this as PouchDoc).toStringForExternal()}, description: ${description}"
}


fun NodeDoc(_id: String, type: String): NodeDoc {
    return GraphElementDoc<NodeDoc>(_id, "N").apply {
        this._id = _id
        this.type = type
    }
}

fun NodeDoc(doc: NodeDoc): NodeDoc {
    return NodeDoc(doc._id, doc.description)
}
