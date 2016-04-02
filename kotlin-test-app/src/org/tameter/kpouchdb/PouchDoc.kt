package org.tameter.kpouchdb

import org.tameter.kotlinjs.Object

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@native("Object")
open class PouchDoc() : Object() {
    var _id: String = noImpl
    var type: String = noImpl
    var rev: String? = noImpl
}

fun <T : PouchDoc> initPouchDoc(doc: T, type: String, _id: String): T {
    return doc.apply {
        this.type = type
        this._id = "${type}_${_id}"
    }
}

// We can't just override toString, because that won't be emitted, because the class is @native.
fun PouchDoc.toStringForNative(): String {
    return "{_id: ${_id}, type: ${type}, rev: ${rev}}"
}