package org.tameter.partialorder.dag

import org.tameter.kotlinjs.JSMapDelegate

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

abstract class GraphElement<TDoc: GraphElementDoc>(
        val graph: Graph,
        internal val doc: TDoc
) {
    var _id: String by JSMapDelegate(doc)
}