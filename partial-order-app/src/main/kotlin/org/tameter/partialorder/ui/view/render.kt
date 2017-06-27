package org.tameter.partialorder.ui.view

import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import org.tameter.kpouchdb.PouchDB
import org.tameter.kpouchdb.remove
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import org.tameter.partialorder.service.proposeEdges
import org.w3c.dom.Element
import kotlin.browser.document

fun render(db: PouchDB, graph: Graph) {
    val appElement = document.getElementById("app") ?: throw Error("Failed to find app element")
    while (appElement.firstChild != null) {
        appElement.removeChild(appElement.firstChild!!)
    }
    val proposedEdges = proposeEdges(graph)
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
    renderNodesByRank(document.getElementById("nodes")!!, graph)
    renderEdges(document.getElementById("edges")!!, db, graph)
    renderProposedEdges(document.getElementById("proposedEdges")!!, db, graph, proposedEdges)
}

fun renderNodesByRank(element: Element, graph: Graph) {
    val nodesByRank = graph.ranks.keys.groupBy { graph.ranks[it] ?: -1 }
    val maxRank = nodesByRank.keys.max() ?: -1
    element.append {
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

fun renderEdges(element: Element, db: PouchDB, graph: Graph) {
    element.append {
        table {
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
                            db.remove(edge.doc)
                        }
                    }
                }
            }
        }
    }
}

fun renderProposedEdges(element: Element, db: PouchDB, graph: Graph, possibleEdges: Collection<Edge>) {
    element.append {
        table {
            tr {
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
                tr {
                    td { +graph.rankById(edge.fromId).toString() }
                    td(classes = "button") {
                        +getNodeDescription(graph, edge.fromId)
                        onClickFunction = {
                            console.log(edge.toPrettyString())
                            db.put(EdgeDoc(edge.fromId, edge.toId))
                        }
                    }
                    td { +graph.rankById(edge.toId).toString() }
                    td(classes = "button") {
                        +getNodeDescription(graph, edge.toId)
                        onClickFunction = {
                            console.log(edge.toPrettyString())
                            db.put(EdgeDoc(edge.toId, edge.fromId))
                        }
                    }
                }
            }
        }
    }
}

private fun getNodeDescription(graph: Graph, fromId: String) = graph.findNodeById(fromId)?.description ?: "???"