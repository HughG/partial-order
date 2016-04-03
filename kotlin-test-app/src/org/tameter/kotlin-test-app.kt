package org.tameter

import org.tameter.kotlinjs.jsobject
import org.tameter.kotlinjs.promise.Promise
import org.tameter.kotlinjs.promise.catchAndLog
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.GraphEdge
import org.tameter.partialorder.dag.GraphNode
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import org.tameter.partialorder.dag.kpouchdb.NodeDoc

fun main(args: Array<String>) {
    initDB().thenP { db ->
        loadGraph(db)
    }.thenV { graph ->
        proposeEdges(graph)
    }.catchAndLog()
}

fun loadGraph(db: PouchDB): Promise<Graph> {
    val g: Graph = Graph()

    console.log("Loading graph ...")

    // Load nodes
    return db.allDocs<NodeDoc>(jsobject {
        startkey = "N_"
        endkey = "N_\uffff"
        include_docs = true
    }).thenV {
        console.log("Nodes:")
        console.log(it)
        it.rows.forEach {
            var node: NodeDoc? = it.doc
            if (node == null) {
                console.log("No node doc in ${it}")
            } else {
                val graphNode = GraphNode(g, node)
                console.log(graphNode.toPrettyString())
                g.addNode(graphNode)
            }
        }
        it
    }.thenP {
        // Load edges
        db.allDocs<EdgeDoc>(jsobject {
            startkey = "E_"
            endkey = "E_\uffff"
            include_docs = true
        })
    }.thenV {
        console.log("Edges:")
        console.log(it)
        it.rows.forEach {
            var edge: EdgeDoc? = it.doc
            if (edge == null) {
                console.log("No edge doc in ${it}")
            } else {
                val graphEdge = GraphEdge(g, edge)
                console.log(graphEdge.toPrettyString())
                g.addEdge(graphEdge)
            }
        }

        console.log("Loading graph ... done.")

        // Return result
        g
    }
}

fun proposeEdges(graph: Graph): Collection<Edge> {
    // Map from a node to all the nodes which have a path to it.
    val allPossibleEdges = mutableSetOf<Edge>()

    // Find set of all possible edges
    for (from in graph.nodes) {
        for (to in graph.nodes) {
            val possibleEdge = Edge(from, to)
            if (!graph.hasPath(to, from) &&
                    !graph.edges.contains(possibleEdge)
//                    !graph.edges.any { it == possibleEdge }
            ) {
                allPossibleEdges.add(possibleEdge)
            }
        }
    }

    fun String.truncateTo(targetLength: Int): String {
        return if (length <= targetLength) this else substring(0, targetLength - 3) + "..."
    }

    console.log("Possible Edges:")
    allPossibleEdges.forEach { edge ->
        val fromNode: GraphNode = graph.nodes.find { it._id == edge.fromId }!!
        val toNode: GraphNode = graph.nodes.find { it._id == edge.toId }!!
        console.log(
                edge.toPrettyString(),
                "'${fromNode.description.truncateTo(15)}' -> '${toNode.description.truncateTo(15)}'"
        )
    }

//    // TODO 2016-04-01 HughG: Would be quicker to just avoid adding these edges in the first place :-)
//    allPossibleEdges.removeAll { possibleE ->
//        graph.edges.any { actualE ->
////            console.log("  Comparing")
////            console.log("    from")
////            console.log("      ${actualE.fromId}")
////            console.log("      ${possibleE.fromId}")
////            console.log("    to")
////            console.log("      ${actualE.toId}")
////            console.log("      ${possibleE.toId}")
//            val result = (actualE == possibleE)
//            if (result) {
//                console.log("Removing ${possibleE.toPrettyString()}: match with ${actualE.toPrettyString()}")
//            }
//            result
//        }
//    }
//    console.log("Remaining Edges:")
//    allPossibleEdges.forEach { console.log(it.toPrettyString()) }

    // Return result
    return allPossibleEdges
}
