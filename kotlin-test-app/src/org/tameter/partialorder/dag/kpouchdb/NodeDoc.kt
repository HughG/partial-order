package org.tameter.partialorder.dag.kpouchdb

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
class NodeDoc(_id: String, var description: String) : GraphElementDoc(_id, "N") {
    override fun toString(): String {
        return "{${super.toString()}; description: ${description}}"
    }
}

fun NodeDoc(doc: NodeDoc): NodeDoc {
    return NodeDoc(doc._id, doc.description)
}
