package org.tameter.partialorder.dag

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class Graph {
    val nodes: MutableSet<GraphNode> = mutableSetOf()
    val edges: MutableSet<GraphEdge> = mutableSetOf()

    fun deepClone(): Graph {
        val g = Graph()
        nodes.forEach { g.nodes.add(GraphNode(g, it)) }
        edges.forEach { g.edges.add(GraphEdge(g, it)) }
        return g
    }
}