package org.tameter.kpouchdb

import org.tameter.kotlin.js.jsobject

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface PouchDoc {
    val _id: String
    var _rev: String
    var _deleted: Boolean?
    val type: String
}

fun <T: PouchDoc> PouchDoc(_id: String, type: String): T {
    return jsobject {
        asDynamic()._id = "${type}_${_id}"
        asDynamic().type = type
    }
}

fun PouchDoc.toStringForExternal(): String {
    return "_id: ${_id}, _rev: ${_rev}, type: ${type}"
}
