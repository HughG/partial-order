package org.tameter.partialorder.dag

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class Graph {
    val nodes: MutableSet<Node> = mutableSetOf()
    val edges: MutableSet<Edge> = mutableSetOf()
}