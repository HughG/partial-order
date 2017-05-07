package org.tameter.partialorder.dag.kpouchdb

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

external class AxisDoc<E>(_id: String) : GraphElementDoc {
//    var edges: Array<E>
}

fun AxisDoc(_id: String): EdgeDoc {
    return GraphElementDoc<EdgeDoc>(_id, "A").apply {
    }
}
