package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kotlinjs.jsobject
import org.tameter.kpouchdb.PouchDoc

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

open external class GraphElementDoc : PouchDoc {
    override var _id: String
    override lateinit var _rev: String
    override var type: String
}

fun <T: GraphElementDoc> GraphElementDoc(_id: String, type: String): T {
    return jsobject<T>().apply {
        this._id = _id
        this.type = type
    }
}
