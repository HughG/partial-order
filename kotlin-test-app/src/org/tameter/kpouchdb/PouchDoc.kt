package org.tameter.kpouchdb

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface PouchDoc {
    var _id: String
    var _rev: String
    var type: String
}

fun PouchDoc.toStringForExternal(): String {
    return "_id: ${_id}, rev: ${_rev}, type: ${type}"
}
