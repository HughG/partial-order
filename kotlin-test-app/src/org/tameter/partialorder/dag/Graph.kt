package org.tameter.partialorder.dag

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class Graph {
    val nodes: MutableSet<Node> = mutableSetOf()
    val edges: MutableSet<Edge> = mutableSetOf()

    fun deepClone(): Graph {
        val g = Graph()
        nodes.forEach { g.nodes.add(Node(g, it)) }
        edges.forEach { g.edges.add(Edge(g, it)) }
        return g
    }
}