package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface AxisDoc<E>: PouchDoc {
//    var edges: Array<E>
}

fun AxisDoc(_id: String): EdgeDoc {
    return PouchDoc<Any>(_id, "A").apply {
    }
}
