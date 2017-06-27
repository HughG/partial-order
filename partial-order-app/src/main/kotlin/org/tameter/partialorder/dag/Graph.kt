package org.tameter.partialorder.dag

import org.tameter.kotlin.collections.MutableMapWithDefault
import org.tameter.kotlin.collections.MutableMultiSet
import org.tameter.kotlin.collections.mutableMultiSetOf
import org.tameter.kotlin.collections.withDefaultValue
import org.tameter.partialorder.util.cached

/**
 * Copyright (c) 2016-2017 Hugh Greene (githugh@tameter.org).
 */

class Graph {
    // --------------------------------------------------------------------------------
    // <editor-fold desc="Properties">

    private val _nodes: MutableMap<String, Node> = mutableMapOf()
    private val _edges: MutableSet<Edge> = mutableSetOf()
    val nodes: Collection<Node> get() = _nodes.values
    val edges: Collection<Edge> get() = _edges

    private val outgoing =
            mutableMapOf<String, MutableSet<Edge>>().withDefaultValue({ mutableSetOf<Edge>() })
    private val descendants =
            mutableMapOf<String, MutableMultiSet<String>>().withDefaultValue({ mutableMultiSetOf<String>() })
    private val ancestors =
            mutableMapOf<String, MutableMultiSet<String>>().withDefaultValue({ mutableMultiSetOf<String>() })

    val roots: Collection<Node>
        get() {
            // TODO 2016-04-03 HughG: Cache, or update it incrementally.
            val result = mutableSetOf<Node>()
            result.addAll(nodes)
            edges.forEach { result.remove(it.to) }
//            console.info("Roots are ${result.joinToString { it._id }}")
            return result
        }

    fun findNodeById(id: String): Node? = _nodes[id]

    // Map from a node to all the nodes which have a path to it.
    private val cachedRanks = cached {
        val ranks = mutableMapOf<Node, Int>()
        for (it in nodes) {
            ranks[it] = 0
        }
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
    // </editor-fold>

    // --------------------------------------------------------------------------------
    // <editor-fold desc="Methods">

    fun addNode(node: Node) {
        cachedRanks.clear()
        _nodes[node._id] = node
    }

    // TODO 2016-04-02 HughG: When implementing removeNode, fail if there are connected edges.

    fun addEdge(edge: Edge) {
        val fromId = edge.fromId
        val toId = edge.toId
        if (hasPath(toId, fromId)) {
            throw Exception("Cannot add edge because it would create a cycle: ${edge}")
        }
        if (!_edges.add(edge)) {
            return
        }

        // Adding a new edge will change the set of which nodes are reachable from where.
        outgoing[fromId].add(edge)

        fun increment(relativeIds: MutableMultiSet<String>, relativeId: String, count: Int) {
            relativeIds.add(relativeId, count)
        }
        adjustCountsForRelatives(toId, fromId, ancestors, ::increment)
        adjustCountsForRelatives(fromId, toId, descendants, ::increment)

        // TODO 2016-04-03 HughG: Instead of just deleting the cache, update it incrementally.
        cachedRanks.clear()
    }

    fun removeEdge(edge: Edge) {
        if (!_edges.remove(edge)) {
            return
        }

        val fromId = edge.fromId
        val toId = edge.toId

        // Removing an edge will change the set of which nodes are reachable from where.
        outgoing[fromId].remove(edge)

        fun decrement(relativeIds: MutableMultiSet<String>, relativeId: String, count: Int) {
            relativeIds.remove(relativeId, count)
        }
        adjustCountsForRelatives(toId, fromId, ancestors, ::decrement)
        adjustCountsForRelatives(fromId, toId, descendants, ::decrement)

        // TODO 2016-04-03 HughG: Instead of just deleting the cache, update it incrementally.
        cachedRanks.clear()
    }

    private fun adjustCountsForRelatives(
            selfId: String,
            otherId: String,
            relatives: MutableMapWithDefault<String, MutableMultiSet<String>>,
            adjust: (MutableMultiSet<String>, String, Int) -> Unit
    ) {
        relatives[selfId].add(otherId)
        val otherNodeRelativeIds = relatives[otherId]
        for (otherNodeRelativeId in otherNodeRelativeIds) {
            val existingPaths = otherNodeRelativeIds.count(otherNodeRelativeId)
            adjust(otherNodeRelativeIds, otherNodeRelativeId, existingPaths)
        }
    }

    fun hasPath(fromId: String, toId: String): Boolean =
            (fromId == toId) || descendants[fromId].contains(toId)

    fun hasPath(from: Node, to: Node): Boolean = hasPath(from._id, to._id)

    fun rank(node: Node): Int {
        return ranks[node] ?: throw Exception("Cannot determine rank of node not in graph: ${node}")
    }

    fun rankById(id: String): Int? {
        return findNodeById(id)?.let { rank(it) }
    }
    // </editor-fold>

    // --------------------------------------------------------------------------------
    // <editor-fold desc="Edge extensions">

    val Edge.from get() = nodeFromGraph("from", doc.fromId)
    val Edge.to get() = nodeFromGraph("to", doc.toId)

    private fun nodeFromGraph(nodeType: String, nodeId: String): Node {
        return findNodeById(nodeId) ?: throw Exception("No '${nodeType}' node ${nodeId}")
    }
    // </editor-fold>

    // --------------------------------------------------------------------------------
    // <editor-fold desc="Node extensions">

    val Node.outgoing: Set<Edge> get() {
        return this@Graph.outgoing[_id]
    }
    // </editor-fold>
}

fun Graph.getAllAddableEdges(): Set<Edge> {
    val allAddableEdges = mutableSetOf<Edge>()

    // Find set of all possible edges
    for (from in nodes) {
        for (to in nodes) {
            val possibleEdge = Edge(from, to)
            if (!hasPath(to, from) && !edges.contains(possibleEdge)) {
                allAddableEdges.add(possibleEdge)
            }
        }
    }
    return allAddableEdges
}
