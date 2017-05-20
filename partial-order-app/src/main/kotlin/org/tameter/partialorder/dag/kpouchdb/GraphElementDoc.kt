package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kotlin.js.jsobject
import org.tameter.kpouchdb.PouchDoc

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface GraphElementDoc : PouchDoc {
    override val _id: String
    override var _rev: String
    override val type: String
}

fun <T: GraphElementDoc> GraphElementDoc(_id: String, type: String): T {
    return jsobject {
        asDynamic()._id = "${type}_${_id}"
        asDynamic().type = type
    }
}
