package org.tameter.partialorder.dag

import org.tameter.kotlinjs.cached

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
        val hasPathFrom = mutableMapOf<GraphNode, MutableCollection<GraphNode>>()
//                .withDefault { mutableListOf() } // doesn't work in JavaScript :-(

//        console.info("Caching paths ...")

        search(SearchType.DepthFirst, roots) { _/*index*/, _/*depth*/, node, _/*prevEdge*/, prevNode ->
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
    val hasPathFrom by cachedHasPathFrom

    // Map from a node to all the nodes which have a path to it.
    private val cachedRanks = cached {
        val ranks = mutableMapOf<Node, Int>()
        console.log("Caching ranks ...")
        search(SearchType.DepthFirst, roots) { index, depth, node, _/*prevEdge*/, prevNode ->
            console.log("${index} ${depth} '${node.description}' <- '${prevNode?.description}'")
            if (!ranks.containsKey(node)) {
                ranks[node] = depth
            }
            VisitResult.Continue
        }
        console.log("Caching ranks ... done")
        ranks
    }
    val ranks by cachedRanks

    val maxRank: Int?
    get() {
        return ranks.values.max()
    }

    fun addNode(node: GraphNode) {
        if (node.graph != this) {
            throw Exception("Cannot add node because it belongs to a different graph: ${node}")
        }
        cachedRanks.clear()
        _nodes[node._id] = node
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
        cachedRanks.clear()
        _edges[edge] = edge
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

    fun hasPath(from: GraphNode, to: GraphNode): Boolean {
        if (from.graph != this || to.graph != this) {
            return false
        }

        if (from == to) {
            return true
        }

        return hasPathFrom[to]?.contains(from) ?: false
    }

    fun rank(node: Node): Int {
        return ranks[node] ?: throw Exception("Cannot determine rank of node not in graph: ${node}")
    }

}