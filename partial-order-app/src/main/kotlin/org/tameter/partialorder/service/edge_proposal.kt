package org.tameter.partialorder.service

import org.tameter.partialorder.dag.CompositeScoring
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import kotlin.js.Math

private fun <T> List<T>.getRandomItem(): T = get((Math.random() * size).toInt())

fun proposeEdges(compositeScoring: CompositeScoring): Collection<Edge> {
    // Find lowest value for 'rank' with more than 1 item.
    // For each 'graph', select 2 random items from this rank for comparison.

    // Note: The following works because the default map implementation in JavaScript iterates over keys
    val allNodesByTotalRank = compositeScoring.nodes.groupBy { compositeScoring.score(it) }
    val sortedRanks = allNodesByTotalRank.keys.sorted()
    for (rank in sortedRanks) {
        val nodesWithEqualRank = allNodesByTotalRank[rank]!!
        if (nodesWithEqualRank.size < 2) {
            continue
        }

        return compositeScoring.scorings.filterIsInstance<Graph>().map { graph ->
            val first = nodesWithEqualRank.getRandomItem()
            var second = first
            while (second == first) {
                second = nodesWithEqualRank.getRandomItem()
            }

            Edge(graph, first, second)
        }
    }

    // Everything is in a unique rank - relax!
    return emptyList()
}
