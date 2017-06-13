package org.tameter

import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import org.tameter.kotlin.js.logError
import org.tameter.kotlin.js.promise.catchAndLog
import org.tameter.kpouchdb.*
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.GraphEdge
import org.tameter.partialorder.dag.GraphNode
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.source.GitHubSource
import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    try {
        val g: Graph = Graph()
        resetDB().thenV { db ->
            val graphUpdater = GraphUpdater(db, g)
            db.liveChanges(ChangeOptions().apply {
                sinceNow()
                include_docs = true
            }).onChange(graphUpdater::handleChange)
            db
        }.thenP { db ->
            GitHubSource("HughG", "partial-order").populate(db)
        }.catchAndLog()
    } catch(e: Error) {
        logError(e)
    }
}

class GraphUpdater(val db: PouchDB, val graph: Graph) {
    private var needsRender = false

    fun handleChange(change: Change) {
        val changeType = if (change.deleted) { "delete" } else { "add/modify" }
        console.log("$changeType ${change.doc?.toStringForExternal()}")
        val doc = change.doc ?: throw Error("Change ${change.id} had no doc")
        when (doc.type) {
            "N" -> {
                val graphNode = GraphNode(graph, doc.unsafeCast<NodeDoc>())
                console.log(graphNode.toPrettyString())
                graph.addNode(graphNode)
            }
            "E" -> {
                val graphEdge = GraphEdge(graph, doc.unsafeCast<EdgeDoc>())
                console.log(graphEdge.toPrettyString())
                graph.addEdge(graphEdge)
            }
            else -> {
                console.warn("Unknown document type ${doc.type}")
            }
        }
        ensureRender()
    }

    fun ensureRender() {
        if (!needsRender) {
            needsRender = true
            window.setTimeout({
                needsRender = false
                render(db, graph)
            }, 0)
        }
    }
}

fun render(db: PouchDB, graph: Graph) {
    val appElement = document.getElementById("app") ?: throw Error("Failed to find app element")
    while (appElement.firstChild != null) {
        appElement.removeChild(appElement.firstChild!!)
    }
    val possibleEdges = proposeEdges(graph)
    renderNodesByRank(appElement, graph)
    renderPossibleEdges(appElement, db, graph, possibleEdges)
}

fun renderNodesByRank(appElement: Element, graph: Graph) {
    val nodesByRank = graph.ranks.keys.groupBy { graph.ranks[it] ?: -1 }
    val maxRank = nodesByRank.keys.max() ?: -1
    appElement.append {
        table {
            tr {
                th { +"Rank" }
                th { +"Source" }
                th { +"Description" }
            }
            for (rank in 0..maxRank) {
                val nodes = nodesByRank[rank] ?: emptyList()
                tr {
                    th {
                        attributes["rowspan"] = nodes.size.toString()
                        +rank.toString()
                    }
                    val node = nodes[0]
                    td { +node.source }
                    td { +node.description }
                }
                for (node in nodes.drop(1)) {
                    tr {
                        td { +node.source }
                        td { +node.description }
                    }
                }
            }
        }
    }
}

fun renderPossibleEdges(appElement: Element, db: PouchDB, graph: Graph, possibleEdges: Collection<Edge>) {
    val groupedEdges = possibleEdges.groupBy { it.fromId }
    appElement.append {
        table {
            tr {
                th { +"From" }
                th { +"To" }
            }
            for ((fromId, edges) in groupedEdges) {
                tr {
                    th {
                        attributes["rowspan"] = edges.size.toString()
                        +getNodeDescription(graph, fromId)
                    }
                    val edge = edges[0]
                    td {
                        +getNodeDescription(graph, edge.toId)
                        onClickFunction = {
                            console.log(edge.toPrettyString())
                            db.put(EdgeDoc(edge.fromId, edge.toId))
                        }
                    }
                }
                for (edge in edges.drop(1)) {
                    tr {
                        td(classes = "button") {
                            onClickFunction = {
                                console.log(edge.toPrettyString())
                                db.put(EdgeDoc(edge.fromId, edge.toId))
                            }
                            +getNodeDescription(graph, edge.toId)
                        }
                    }
                }
            }
        }
    }
}

private fun getNodeDescription(graph: Graph, fromId: String) = graph.findNodeById(fromId)?.description ?: "???"

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

    // Return result
    return sortedPossibleEdges
}
