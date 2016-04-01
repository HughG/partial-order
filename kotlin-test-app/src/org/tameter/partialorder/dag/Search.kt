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
        roots: Collection<Node>,
        fn: (index: Int, depth: Int, node: Node, prevEdge: Edge?, prevNode: Node?) -> VisitResult = {
            index, depth, node, prevEdge, prevNode -> VisitResult.Continue
        }
): SearchResult {
    var queue = mutableListOf<Node>();
    var connectedNodes = mutableListOf<Node>();
    var connectedBy = mutableMapOf<String, Edge>();
    var id2depth = mutableMapOf<String, Int>();
    var visited = mutableSetOf<String>();
    var index = 0;
    var found: Node? = null;

    // enqueue v
    for (root in roots) {
        queue.add(0, root)
        if (searchType == SearchType.BreadthFirst) {
            visited.add(root._id)
            connectedNodes.add(root)
        }
        id2depth[root._id] = 0
    }

    while (queue.size != 0) {
        var v = when (searchType) {
            SearchType.BreadthFirst -> queue.removeAt(0)
            SearchType.DepthFirst -> queue.removeAt(queue.size)
        }

        if (searchType == SearchType.DepthFirst) {
            if (visited.add(v._id)) {
                connectedNodes.add(connectedNodes.size, v);
            }
        }

        var depth = id2depth[v._id];
        var prevEdge = connectedBy[v._id];
        var prevNode = prevEdge?.from
        var ret = fn(index++, depth!!, v, prevEdge, prevNode);
        if (ret == VisitResult.Return) {
            found = v
            break
        }
        if (ret == VisitResult.Cancel) {
            break
        }

        var vwEdges: Collection<Edge> = v.outgoing()
        for (e in vwEdges) {
            var w = e.to
            queue.add(queue.size, w)

            if (searchType == SearchType.BreadthFirst) {
                visited.add(w._id)
                connectedNodes.add(connectedNodes.size, w)
            }

            connectedBy[w._id] = e

            id2depth[w._id] = (id2depth[w._id] ?: 0) + 1
        }
    }

    var connectedEles = mutableListOf<Edge>()

    for (node in connectedNodes) {
        var edge = connectedBy[node._id];
        if (edge != null) {
            connectedEles.add(edge)
        }
    }

    return SearchResult(connectedEles, found)
};
