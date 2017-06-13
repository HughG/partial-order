package org.tameter.kpouchdb

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface PouchDoc {
    val _id: String
    var _rev: String
    var _deleted: Boolean?
    val type: String
}

fun PouchDoc.toStringForExternal(): String {
    return "_id: ${_id}, _rev: ${_rev}, type: ${type}"
}
