package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.toStringForNative

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
class NodeDoc(_id: String, var description: String) : GraphElementDoc(_id, "N")

fun NodeDoc(doc: NodeDoc): NodeDoc {
    return NodeDoc(doc._id, doc.description)
}

// We can't just override toString, because that won't be emitted, because the class is @native.
fun NodeDoc.toStringForNative(): String {
    return "{${this.toStringForNative()}; description: ${description}}"
}
