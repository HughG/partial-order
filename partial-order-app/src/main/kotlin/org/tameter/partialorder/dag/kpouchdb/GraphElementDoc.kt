package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface GraphElementDoc : PouchDoc

fun <T: GraphElementDoc> GraphElementDoc(_id: String, type: String): T {
    return PouchDoc(_id, type)
}
