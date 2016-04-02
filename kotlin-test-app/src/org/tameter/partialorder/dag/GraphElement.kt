package org.tameter.partialorder.dag

import org.tameter.kotlinjs.JSMapDelegate
import org.tameter.partialorder.dag.kpouchdb.GraphElementDoc

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

abstract class GraphElement<TDoc: GraphElementDoc>(
        val graph: Graph,
        internal val doc: TDoc
) {
    var _id: String by JSMapDelegate(doc)

    abstract fun toPrettyString(): String
}