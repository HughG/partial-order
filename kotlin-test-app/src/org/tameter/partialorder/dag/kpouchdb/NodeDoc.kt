package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc
import org.tameter.kpouchdb.initPouchDoc
import org.tameter.kpouchdb.toStringForNative

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@native("Object")
class NodeDoc() : GraphElementDoc() {
    var description: String
}

fun NodeDoc(_id: String): NodeDoc {
    return initPouchDoc(NodeDoc(), "N", _id)
}

fun NodeDoc(doc: NodeDoc): NodeDoc {
    return (NodeDoc(doc._id)).apply {
        description = doc.description
    }
}

// We can't just override toString, because that won't be emitted, because the class is @native.
fun NodeDoc.toStringForNative(): String {
    return "{${(this as PouchDoc).toStringForNative()}; description: ${description}}"
}
