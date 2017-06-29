package org.tameter.partialorder.service

import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.MultiGraph
import kotlin.js.Math

fun proposeEdges(graphs: MultiGraph, nodeCombinedRanks: Map<String, Int>): Collection<Edge> {
    val allGraphsAddableEdges = graphs.graphs.flatMapTo(HashSet<Edge>(), Graph::getAllAddableEdges)
    return sortEdges(allGraphsAddableEdges, nodeCombinedRanks)
}

private fun sortEdges(
        allPossibleEdges: Set<Edge>,
        nodeCombinedRanks: Map<String, Int>
): Collection<Edge> {
    // Group edges by "from node rank", then within that by "to node rank", but randomise each inner list.  This means
    // that the user is given edges in an order which will tend to push nodes out of lower ranks (due to the outer,
    // sorted grouping) but also tend to introduce edges between nodes which will end up (after many edges are added)
    // being in different ranks (due to the randomisation of the inner lists).  If the inner lists were not randomised,
    // you would tend to just add links from one node to lots of other nodes, giving a "flat" graph, whereas we want a
    // "deep" graph so that nodes quickly become more strongly ordered.

    fun Iterable<Edge>.groupByRank(getNodeId: (Edge) -> String): LinkedHashMap<Int, MutableList<Edge>> {
        return groupByTo(LinkedHashMap<Int, MutableList<Edge>>()) { nodeCombinedRanks[getNodeId(it)]!! }
    }

    fun removeSameOrHigherRankedReverseEdges(edgesByFromRank: LinkedHashMap<Int, MutableList<Edge>>) {
        fun findReverseEdge(edgesFromThisRank: MutableList<Edge>, edgesFromOtherRank: MutableList<Edge>): Edge? {
            return edgesFromOtherRank.find { fromOtherRank ->
                edgesFromThisRank.any { fromThisRank ->
                    fromThisRank.fromId == fromOtherRank.toId && fromThisRank.toId == fromOtherRank.fromId
                }
            }
        }

        val lastIndex = edgesByFromRank.size - 1
        edgesByFromRank.values.forEachIndexed { index, edgesFromThisRank ->
            for (i in index..lastIndex) {
                val edgesFromOtherRank = edgesByFromRank[i] ?: continue
                var reverseEdge = findReverseEdge(edgesFromThisRank, edgesFromOtherRank)
                while (reverseEdge != null) {
                    edgesFromOtherRank.remove(reverseEdge)
                    reverseEdge = findReverseEdge(edgesFromThisRank, edgesFromOtherRank)
                }
            }
        }
    }

    fun <K: Comparable<K>, V> HashMap<K, V>.sortedByKey(): LinkedHashMap<K, V> {
        val result = linkedMapOf<K, V>()
        for (key in keys.sorted()) {
            result[key] = get(key)!!
        }
        return result
    }

    return allPossibleEdges.groupByRank(Edge::fromId)
            .sortedByKey()
            .apply(::removeSameOrHigherRankedReverseEdges)
            .values
            .flatMap { edgesFromRank ->
                val uniqueFromIdCount = edgesFromRank.distinctBy(Edge::fromId).size
                // If there's only one node at a given rank, don't propose adding edges from it to other nodes, because
                // we've already narrowed down that rank as much as we can.
                if (uniqueFromIdCount == 1) {
                    mutableListOf<Edge>()
                } else {
                    edgesFromRank.groupByRank(Edge::toId)
                            .sortedByKey()
                            .values
                            .flatMap { shuffle(it); it }
                }
            }
}

fun <T> shuffle(list: MutableList<T>) {
    val size = list.size
    for (i in 0 until size) {
        val j = (Math.random() * size).toInt()
        val tmp = list[i]
        list[i] = list[j]
        list[j] = tmp
    }
}

fun Graph.getAllAddableEdges(): Set<Edge> {
    val allAddableEdges = mutableSetOf<Edge>()

    // Find set of all possible edges
    for (from in nodes) {
        for (to in nodes) {
            if (!hasPath(to, from) && !from.hasEdgeTo(to)) {
                allAddableEdges.add(Edge(this, from, to))
            }
        }
    }
    return allAddableEdges
}
