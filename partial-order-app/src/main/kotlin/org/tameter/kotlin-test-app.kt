package org.tameter

import kotlinx.html.dom.append
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import org.tameter.kotlin.js.promise.Promise
import org.tameter.kotlin.js.promise.catchAndLog
import org.tameter.kpouchdb.AllDocsOptions
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.*
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import kotlin.browser.document
import kotlin.js.Math

fun main(args: Array<String>) {
    initDB().thenP { db ->
        loadGraph(db)
    }.thenV { graph ->
        listByRank(graph)
        graph
    }.thenV { graph ->
        val possibleEdges = proposeEdges(graph)
        val randomIndex = Math.floor(Math.random() * possibleEdges.size)
        val edge = possibleEdges.drop(randomIndex).firstOrNull()
        if (edge != null) {
            val graphEdge = GraphEdge(graph, edge)
            graph.addEdge(graphEdge)
            console.log("Added ${graphEdge}")
        } else {
            console.log("No edges to add")
        }
        listByRank(graph)
        renderNodesByRank(graph)
    }.catchAndLog()
}

fun listByRank(graph: Graph) {
    console.info("Nodes by rank ...")
    val nodesByRank = graph.ranks.keys.groupBy { graph.ranks[it] ?: -1 }
    val maxRank = nodesByRank.keys.max() ?: -1
    for (rank in 0..maxRank) {
        val nodes = nodesByRank[rank] ?: emptyList()
        console.info("${rank}: ${nodes.map(Node::description).joinToString()}")
    }
}

fun renderNodesByRank(graph: Graph) {
    val appElement = document.getElementById("app") ?: throw Error("Failed to find app element")
    val nodesByRank = graph.ranks.keys.groupBy { graph.ranks[it] ?: -1 }
    val maxRank = nodesByRank.keys.max() ?: -1
    appElement.append {
        table {
            for (rank in 0..maxRank) {
                val nodes = nodesByRank[rank] ?: emptyList()
                tr {
                    th {
                        attributes["rowspan"] = nodes.size.toString()
                        +rank.toString()
                    }
                    td { +nodes[0].description }
                }
                for (node in nodes.drop(1)) {
                    tr {
                        td { +node.description }
                    }
                }
            }
        }
    }
}

fun loadGraph(db: PouchDB): Promise<Graph> {
    val g: Graph = Graph()

    console.log("Loading graph ...")

    // Load nodes
    return db.allDocs<NodeDoc>(AllDocsOptions().apply {
        startkey = "N_"
        endkey = "N_\uffff"
        include_docs = true
    }).thenV {
        console.log("Nodes:")
        console.log(it)
        it.rows.forEach {
            val node: NodeDoc? = it.doc
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
        db.allDocs<EdgeDoc>(AllDocsOptions().apply {
            startkey = "E_"
            endkey = "E_\uffff"
            include_docs = true
        })
    }.thenV {
        console.log("Edges:")
        console.log(it)
        it.rows.forEach {
            val edge: EdgeDoc? = it.doc
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
            ) {
                allPossibleEdges.add(possibleEdge)
            }
        }
    }

    // Sort edges by how much they will reduce the number of edges in each rank, starting with the
    // lowest rank.  In other words, sort them by "the rank of the 'to' node and then the reverse of
    // the rank of the 'from' node", because then adding that edge will move higher-up nodes
    // furthest down the graph.
    val maxRank = graph.maxRank
    console.log("Max rank = ${maxRank}")
    val sortedPossibleEdges: Collection<Edge> =
        if (maxRank != null) {
            allPossibleEdges.sortedWith(compareBy<Edge>(
                    { graph.rank(graph.findNodeById(it.toId)!!) },
                    { maxRank - graph.rank(graph.findNodeById(it.fromId)!!) }
            ))
        } else {
            emptyList()
        }

    // Dump the edges to the console
    fun String.truncateTo(targetLength: Int): String {
        return if (length <= targetLength) this else substring(0, targetLength - 3) + "..."
    }

    console.log("Possible Edges:")
    sortedPossibleEdges.forEach { edge ->
        val fromNode: GraphNode = graph.findNodeById(edge.fromId)!!
        val toNode: GraphNode = graph.findNodeById(edge.toId)!!
        console.log(
                edge.toPrettyString(),
                "'${fromNode.description.truncateTo(15)}' -> '${toNode.description.truncateTo(15)}'"
        )
    }

    // Return result
    return allPossibleEdges
}
