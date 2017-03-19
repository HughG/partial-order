package org.tameter.kpouchdb

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */
open class AbstractPouchDoc(override var _id: String, override var type: String) : PouchDoc {
    override var rev: String? = null
}

// TODO 2017-04-22 hughg: Can we now override toString, given this is no longer @native?
// We can't just override toString, because that won't be emitted, because the class is @native.
fun AbstractPouchDoc.toStringForNative(): String {
    return "{_id: ${_id}, type: ${type}, rev: ${rev}}"
}
