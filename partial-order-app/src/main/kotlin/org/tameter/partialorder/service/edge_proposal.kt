package org.tameter.partialorder.service

import org.tameter.partialorder.dag.CompositeScoring
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import kotlin.js.Math

private fun <T>randomItem(list: List<T>): T = list[(Math.random() * list.size).toInt()]

fun proposeEdges(compositeScoring: CompositeScoring): Collection<Edge> {
    // Find lowest value for 'rank' with more than 1 item
    // For each 'graph', select 2 random items from this rank for comparison

    val allNodesByTotalRank = compositeScoring.nodes
            .sortedBy { compositeScoring.score(it) }
            .groupBy { compositeScoring.score(it) }
    for (nodesWithEqualRank in allNodesByTotalRank.values) {
        if(nodesWithEqualRank.size < 2) {
            continue
        }

        val list = ArrayList<Edge>()
        compositeScoring.scorings.filterIsInstance<Graph>().forEach {
            val first = randomItem(nodesWithEqualRank)
            var second = first
            while(second == first) {
                second = randomItem(nodesWithEqualRank)
            }

            list.add(Edge(it, first, second))
        }
        return list
    }

    // Everything is in a unique rank - relax!
    return emptyList()
}
