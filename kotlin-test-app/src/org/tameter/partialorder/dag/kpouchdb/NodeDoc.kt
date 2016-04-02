package org.tameter.partialorder.dag

import org.tameter.kpouchdb.initPouchDoc

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