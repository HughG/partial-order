package org.tameter.partialorder.ui.view

import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.*
import org.tameter.partialorder.service.proposeEdges
import org.w3c.dom.Element
import kotlin.browser.document

fun render(db: PouchDB, graphs: MultiGraph) {
    val appElement = document.getElementById("app") ?: throw Error("Failed to find app element")
    while (appElement.firstChild != null) {
        appElement.removeChild(appElement.firstChild!!)
    }
    appElement.append {
        div {
            div {
                id = "main"
                div { id = "nodes" }
                div { id = "edges" }
            }
            div { id = "proposedEdges" }
        }
    }
    val nodeCombinedRanks = getCombinedRanks(graphs)
    val nodesByCombinedRank = graphs.nodes.groupBy { nodeCombinedRanks[it._id]!! }
    renderNodesByRank(document.getElementById("nodes")!!, nodesByCombinedRank)
    renderEdges(document.getElementById("edges")!!, db, graphs)
    val proposedEdges = proposeEdges(graphs, nodeCombinedRanks)
    renderProposedEdges(document.getElementById("proposedEdges")!!, db, graphs, proposedEdges, nodeCombinedRanks)
}

fun getCombinedRanks(graphs: MultiGraph): Map<String, Int> {
    return graphs.nodes.associate { node -> node._id to graphs.graphs.map { g -> g.rank(node) }.sum() }
}

fun renderNodesByRank(element: Element, nodesByCombinedRank: Map<Int, List<Node>>) {
    element.append {
        table {
            tr {
                th { +"Rank" }
                th { +"Source" }
                th { +"Description" }
            }
            for (rank in nodesByCombinedRank.keys.sorted()) {
                val nodes = nodesByCombinedRank[rank] ?: continue
                if (nodes.isEmpty()) {
                    console.warn("Empty node list for rank $rank")
                    continue
                }
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

fun renderEdges(element: Element, db: PouchDB, graphs: MultiGraph) {
    element.append {
        table {
            tr {
                for (graph in graphs.graphs) {
                    td { renderEdges(this, db, graph) }
                }
            }
        }
    }
}

fun renderEdges(element: HtmlBlockTag, db: PouchDB, graph: Graph) {
    element.table {
        tr {
            th {
                attributes["colspan"] = "2"
                +"From"
            }
            th {
                attributes["colspan"] = "2"
                +"To"
            }
            th {
                +"Remove"
            }
        }
        for (edge in graph.edges) {
            tr {
                td { +graph.rankById(edge.fromId).toString() }
                td { +getNodeDescription(graph, edge.fromId) }
                td { +graph.rankById(edge.toId).toString() }
                td { +getNodeDescription(graph, edge.toId) }
                td(classes = "button") {
                    +"[X]"
                    onClickFunction = {
                        console.log(edge.toPrettyString())
                        edge.remove(db)
                    }
                }
            }
        }
    }
}

fun renderProposedEdges(
        element: Element,
        db: PouchDB,
        graphs: MultiGraph,
        possibleEdges: Collection<Edge>,
        nodeCombinedRanks: Map<String, Int>
) {
    element.append {
        table {
            tr {
                th {
                    +"Axis"
                }
                th {
                    attributes["colspan"] = "2"
                    +"From"
                }
                th {
                    attributes["colspan"] = "2"
                    +"To"
                }
            }
            for (edge in possibleEdges) {
                val graph = graphs.findGraphById(edge.graphId)!!
                tr {
                    td { +graph.id }
                    td { +nodeCombinedRanks[edge.fromId].toString() }
                    td(classes = "button") {
                        +getNodeDescription(graph, edge.fromId)
                        onClickFunction = {
                            console.log(edge.toPrettyString())
                            edge.store(db)
                        }
                    }
                    td { +nodeCombinedRanks[edge.toId].toString() }
                    td(classes = "button") {
                        +getNodeDescription(graph, edge.toId)
                        onClickFunction = {
                            console.log(edge.toPrettyString())
                            edge.reverse().store(db)
                        }
                    }
                }
            }
        }
    }
}

private fun getNodeDescription(graph: Graph, fromId: String) = graph.findNodeById(fromId)?.description ?: "???"