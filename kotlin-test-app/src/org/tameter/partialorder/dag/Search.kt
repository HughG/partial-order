package org.tameter.partialorder.dag

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 *
 * Adapted from cytoscape.js/src/collection/algorithms/bfs-dfs.js
 * at https://github.com/cytoscape/cytoscape.js/commit/efb6f7311615137b6510838f1b18f3bc170f9511
 * which itself says it is "from pseudocode on wikipedia".
 */

enum class SearchType {
    BreadthFirst,
    DepthFirst
}

enum class VisitResult {
    Return,
    Continue,
    Cancel
}

data class SearchResult (
    val path: Collection<Edge>,
    val found: Node?
)

fun Graph.search(
        searchType: SearchType,
        roots: Collection<GraphNode>,
        fn: (index: Int, depth: Int, node: GraphNode, prevEdge: GraphEdge?, prevNode: GraphNode?) -> VisitResult = {
            index, depth, node, prevEdge, prevNode -> VisitResult.Continue
        }
): SearchResult {
    var queue = mutableListOf<GraphNode>()
    var connectedNodes = mutableListOf<GraphNode>()
    var connectedBy = mutableMapOf<String, GraphEdge>()
    var id2depth = mutableMapOf<String, Int>()
    var visited = mutableSetOf<String>()
    var index = 0
    var found: GraphNode? = null

    // enqueue v
    for (root in roots) {
        queue.add(0, root)
        if (searchType == SearchType.BreadthFirst) {
            visited.add(root._id)
            connectedNodes.add(root)
        }
        id2depth[root._id] = 0
    }

//    console.log("Initial queue: ${queue.joinToString { it._id }}")
//    console.log("Initial depths: ${id2depth.entries.joinToString()}")

    while (queue.size != 0) {
        var v = when (searchType) {
            SearchType.BreadthFirst -> queue.removeAt(0)
            SearchType.DepthFirst -> queue.removeAt(queue.size - 1)
        }

//        console.log("Visiting ${v._id}")

        if (searchType == SearchType.DepthFirst) {
            if (visited.add(v._id)) {
//                console.log("First visit to ${v._id}")
                connectedNodes.add(connectedNodes.size, v)
//                console.log("Added to connectedNodes ${v._id}")
            }
        }

        var depthQ = id2depth[v._id]
//        console.log("Depth for ${v._id} is ${depthQ}")
        var depth = depthQ!!
        var prevEdge = connectedBy[v._id]
        var prevNode = prevEdge?.from
//        console.log("fn(${index + 1}, ${depth}, ${v._id}, ${prevEdge?._id}, ${prevNode?._id})")
        var ret = fn(index++, depth, v, prevEdge, prevNode)
//        console.log("fn returned ${ret.name}")
        if (ret == VisitResult.Return) {
            found = v
            break
        }
        if (ret == VisitResult.Cancel) {
            break
        }

        var vwEdges: Collection<GraphEdge> = v.outgoing()
//        console.log("Enqueueing ${vwEdges.joinToString {it._id}}")
        for (e in vwEdges) {
            var w = e.to
            queue.add(queue.size, w)

            if (searchType == SearchType.BreadthFirst) {
                visited.add(w._id)
                connectedNodes.add(connectedNodes.size, w)
//                console.log("Added to connectedNodes ${w._id}")
            }

            connectedBy[w._id] = e

            id2depth[w._id] = (id2depth[v._id] ?: 0) + 1
        }
    }

    var connectedEles = mutableListOf<Edge>()

    for (node in connectedNodes) {
        var edge = connectedBy[node._id]
        if (edge != null) {
            connectedEles.add(edge)
        }
    }

    return SearchResult(connectedEles, found)
}
