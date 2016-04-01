package org.tameter.partialorder.dag

import org.tameter.kpouchdb.PouchDoc
import org.tameter.kpouchdb.initPouchDoc

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@native("Object")
class Node : PouchDoc() {
    var description: String

}

fun Node(_id: String): Node {
    return initPouchDoc(Node(), "N", _id)
}