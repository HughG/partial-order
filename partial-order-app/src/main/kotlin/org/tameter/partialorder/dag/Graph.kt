package org.tameter.partialorder.dag

import org.tameter.partialorder.util.cached

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class Graph {
    private val _nodes: MutableMap<String, GraphNode> = mutableMapOf()
    private val _edges: MutableMap<Edge, GraphEdge> = mutableMapOf()
    val nodes: Collection<GraphNode>
            get() = _nodes.values
    val edges: Collection<GraphEdge>
            get() = _edges.values

    fun findNodeById(id: String): GraphNode? = _nodes[id]
    fun findEdge(edge: Edge): GraphEdge? = _edges[edge]

    // Map from a node to all the nodes which have a path to it.
    private val cachedHasPathFrom = cached {
        val hasPathFrom = mutableMapOf<String, MutableCollection<String>>()
//                .withDefault { mutableListOf() } // doesn't work in JavaScript :-(

//        console.info("Caching paths ...")

        search(SearchType.DepthFirst) { _/*index*/, _/*depth*/, node, _/*prevEdge*/, prevNode ->
            val hasPathFromNode = hasPathFrom.getOrPut(node._id, { mutableListOf() })
//            console.log("hasPathFromNodeQ = ${hasPathFromNodeQ}")
//            console.log("hasPathFrom = ${hasPathFrom.entries.joinToString()}")
            if (prevNode != null) {
                hasPathFromNode.add(prevNode._id)
//                console.info("Path to ${node._id} from ${prevNode._id}")
                val hasPathsFromPrevNode = hasPathFrom[prevNode._id]!!
                hasPathFromNode.addAll(hasPathsFromPrevNode)
//                console.info("Path to ${node._id} from ${hasPathsFromPrevNode.joinToString {it._id}}")
            }

            VisitResult.Continue
        }

//        console.info("Caching paths ... done.")

        hasPathFrom
    }
    val hasPathFrom by cachedHasPathFrom

    // Map from a node to all the nodes which have a path to it.
    private val cachedRanks = cached {
        val ranks = mutableMapOf<Node, Int>()
        nodes.forEach { ranks[it] = 0 }
        console.log("Caching ranks ...")
        val edgesFromNode = edges.groupBy { it.from }
        val nodesToProcess = mutableListOf<Node>().apply { addAll(roots) }
        while (nodesToProcess.isNotEmpty()) {
            val fromNode = nodesToProcess.removeAt(0)
            edgesFromNode[fromNode]?.forEach { edge ->
                val toNode = edge.to
                ranks[toNode] = maxOf(ranks[toNode]!!, ranks[fromNode]!! + 1)
                console.log("${ranks[toNode]} '${toNode.description}' <- '${fromNode.description}'")
                nodesToProcess.add(toNode)
            }
        }
        console.log("Caching ranks ... done")
        ranks
    }
    val ranks by cachedRanks

    val maxRank: Int?
    get() {
        return ranks.values.max()
    }

    fun addNode(node: Node) {
        if (node is GraphNode && node.graph != this) {
            throw Exception("Cannot add node because it belongs to a different graph: ${node}")
        }
        cachedRanks.clear()
        _nodes[node._id] = node as? GraphNode ?: GraphNode(this, node)
    }

    // TODO 2016-04-02 HughG: When implementing removeNode, fail if there are connected edges.

    fun addEdge(edge: Edge) {
        if (edge is GraphEdge && edge.graph != this) {
            throw Exception("Cannot add edge because it belongs to a different graph: ${edge}")
        }
        // TODO 2017-06-13 HughG: Return or throw if the edge already exists.
        if (hasPath(edge.toId, edge.fromId)) {
            throw Exception("Cannot add edge because it would create a cycle: ${edge}")
        }
        // Adding a new edge will change the set of which nodes are reachable from where.
        // TODO 2016-04-03 HughG: Instead of just deleting the cache, update it incrementally.
        cachedHasPathFrom.clear()
        cachedRanks.clear()
        _edges[edge] = edge as? GraphEdge ?: GraphEdge(this, edge)
    }

    fun removeEdge(edge: GraphEdge): Boolean {
        if (edge.graph != this) {
//            throw Exception("Cannot remove edge because it belongs to a different graph: ${edge}")
        }
        val removed = (_edges.remove(edge) != null)
        if (removed) {// Adding a new edge will change the set of which nodes are reachable from where.
            // TODO 2016-04-03 HughG: Instead of just deleting the cache, update it incrementally.
            cachedHasPathFrom.clear()
            cachedRanks.clear()
        }
        return removed
    }

//    fun deepClone(): Graph {
//        val g = Graph()
//        nodes.forEach { g._nodes.add(GraphNode(g, it)) }
//        edges.forEach { g._edges.add(GraphEdge(g, it)) }
//        return g
//    }

    val roots: Collection<GraphNode>
        get() {
            // TODO 2016-04-03 HughG: Cache, or update it incrementally.
            val result = mutableSetOf<GraphNode>()
            result.addAll(nodes)
            edges.forEach { result.remove(it.to) }
//            console.info("Roots are ${result.joinToString { it._id }}")
            return result
        }

    fun hasPath(fromId: String, toId: String): Boolean {
        if (fromId == toId) {
            return true
        }

        return hasPathFrom[toId]?.contains(fromId) ?: false
    }

    fun hasPath(from: Node, to: Node): Boolean = hasPath(from._id, to._id)


    fun rank(node: Node): Int {
        return ranks[node] ?: throw Exception("Cannot determine rank of node not in graph: ${node}")
    }

    fun rankById(id: String): Int? {
        return findNodeById(id)?.let { rank(it) }
    }
}

fun Graph.getAllAddableEdges(): Set<Edge> {
    val allPossibleEdges = mutableSetOf<Edge>()

    // Find set of all possible edges
    for (from in nodes) {
        for (to in nodes) {
            val possibleEdge = Edge(from, to)
            if (!hasPath(to, from) && !edges.contains(possibleEdge)) {
                allPossibleEdges.add(possibleEdge)
            }
        }
    }
    return allPossibleEdges
}
