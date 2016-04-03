package org.tameter.partialorder.dag

import org.tameter.kotlinjs.cached

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class Graph {
    private val _nodes: MutableSet<GraphNode> = mutableSetOf()
    private val _edges: MutableSet<GraphEdge> = mutableSetOf()
    val nodes: Set<GraphNode> = _nodes
    val edges: Set<GraphEdge> = _edges

    // Map from a node to all the nodes which have a path to it.
    private val cachedHasPathFrom = cached {
        val hasPathFrom = mutableMapOf<GraphNode, MutableCollection<GraphNode>>()
//                .withDefault { mutableListOf() } // doesn't work in JavaScript :-(

//        console.info("Caching paths ...")

        search(SearchType.DepthFirst, roots) { index, depth, node, prevEdge, prevNode ->
            var hasPathFromNodeQ = hasPathFrom[node]
//            console.log("hasPathFromNodeQ = ${hasPathFromNodeQ}")
//            console.log("hasPathFrom = ${hasPathFrom.entries.joinToString()}")
            if (hasPathFromNodeQ == null) {
                hasPathFrom[node] = mutableListOf()
                hasPathFromNodeQ = hasPathFrom[node]
            }
//            console.log("hasPathFromNodeQ = ${hasPathFromNodeQ}")
//            console.log("hasPathFrom = ${hasPathFrom.entries.joinToString()}")
            val hasPathFromNode = hasPathFromNodeQ!!
            if (prevNode != null) {
                hasPathFromNode.add(prevNode)
//                console.info("Path to ${node._id} from ${prevNode._id}")
                val hasPathsFromPrevNode = hasPathFrom[prevNode]!!
                hasPathFromNode.addAll(hasPathsFromPrevNode)
//                console.info("Path to ${node._id} from ${hasPathsFromPrevNode.joinToString {it._id}}")
            }

            VisitResult.Continue
        }

//        console.info("Caching paths ... done.")

        hasPathFrom
    }
    private val hasPathFrom by cachedHasPathFrom

    fun addNode(node: GraphNode) {
        if (node.graph != this) {
            throw Exception("Cannot add node because it belongs to a different graph: ${node}")
        }
        _nodes.add(node)
    }

    // TODO 2016-04-02 HughG: When implementing removeNode, fail if there are connected edges.

    fun addEdge(edge: GraphEdge) {
        if (edge.graph != this) {
            throw Exception("Cannot add edge because it belongs to a different graph: ${edge}")
        }
        if (hasPath(edge.to, edge.from)) {
            throw Exception("Cannot add edge because it would create a cycle: ${edge}")
        }
        // Adding a new edge will change the set of which nodes are reachable from where.
        // TODO 2016-04-03 HughG: Instead of just deleting the cache, update it incrementally.
        cachedHasPathFrom.clear()
        _edges.add(edge)
    }

    fun deepClone(): Graph {
        val g = Graph()
        nodes.forEach { g._nodes.add(GraphNode(g, it)) }
        edges.forEach { g._edges.add(GraphEdge(g, it)) }
        return g
    }

    val roots: Collection<GraphNode>
        get() {
            // TODO 2016-04-03 HughG: Cache, or update it incrementally.
            val result = mutableSetOf<GraphNode>()
            result.addAll(nodes)
            edges.forEach { result.remove(it.to) }
//            console.info("Roots are ${result.joinToString { it._id }}")
            return result
        }

    fun hasPath(from: GraphNode, to: GraphNode): Boolean {
        if (from.graph != this || to.graph != this) {
            return false
        }

        if (from == to) {
            return true
        }

        return hasPathFrom[to]?.contains(from) ?: false
    }
}