package org.tameter.partialorder.dag.kpouchdb

import org.tameter.kpouchdb.PouchDoc

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external interface AxisDoc: PouchDoc {
//    var edges: Array<E>
}

fun AxisDoc(_id: String): AxisDoc {
    return PouchDoc<AxisDoc>(_id, "A").apply {
    }
}
