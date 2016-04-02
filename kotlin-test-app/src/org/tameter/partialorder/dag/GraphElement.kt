package org.tameter.partialorder.dag

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

abstract class GraphElement<TDoc: GraphElementDoc>(
        val graph: Graph,
        internal val doc: TDoc
) {
}