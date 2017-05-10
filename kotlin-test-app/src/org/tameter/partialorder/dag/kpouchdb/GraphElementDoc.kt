package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kotlin.js.jsobject
import org.tameter.kpouchdb.PouchDoc

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface GraphElementDoc : PouchDoc {
    override var _id: String
    override var _rev: String
    override var type: String
}

fun <T: GraphElementDoc> GraphElementDoc(_id: String, type: String): T {
    return jsobject<T>().apply {
        this._id = "${type}_${_id}"
        this.type = type
    }
}
