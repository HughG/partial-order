package org.tameter

import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import org.tameter.kotlin.js.doOrLogError
import org.tameter.kotlin.js.promise.catchAndLog
import org.tameter.kpouchdb.*
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.Node
import org.tameter.partialorder.dag.getAllAddableEdges
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.source.GitHubSource
import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.browser.window
import kotlin.collections.set
import kotlin.js.Math

fun main(args: Array<String>) {
    doOrLogError {
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
    }
}

class GraphUpdater(val db: PouchDB, val graph: Graph) {
    private var needsRender = false

    fun handleChange(change: Change) = doOrLogError {
        doHandleChange(change)
    }

    private fun doHandleChange(change: Change) {
        val changeType = if (change.deleted) { "delete" } else { "add/modify" }
        console.log("$changeType ${change.doc?.toStringForExternal()}")
        val doc = change.doc ?: throw Error("Change ${change.id} had no doc")
        when (doc.type) {
            "N" -> {
                val graphNode = Node(doc.unsafeCast<NodeDoc>())
                console.log(graphNode.toPrettyString())
                if (change.deleted) {
                    TODO("graph.removeNode($graphNode)")
                } else {
                    graph.addNode(graphNode)
                }
            }
            "E" -> {
                val graphEdge = Edge(doc.unsafeCast<EdgeDoc>())
                console.log(graphEdge.toPrettyString())
                if (change.deleted) {
                    graph.removeEdge(graphEdge)
                } else {
                    graph.addEdge(graphEdge)
                }
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

fun proposeEdges(graph: Graph): Collection<Edge> {
    return sortEdges(graph, graph.getAllAddableEdges())
}

private fun sortEdges(graph: Graph, allPossibleEdges: Set<Edge>): Collection<Edge> {
    // Group edges by "from node rank", then within that by "to node rank", but randomise each inner list.  This means
    // that the user is given edges in an order which will tend to push nodes out of lower ranks (due to the outer,
    // sorted grouping) but also tend to introduce edges between nodes which will end up (after many edges are added)
    // being in different ranks (due to the randomisation of the inner lists).  If the inner lists were not randomised,
    // you would tend to just add links from one node to lots of other nodes, giving a "flat" graph, whereas we want a
    // "deep" graph so that nodes quickly become more strongly ordered.

    fun Iterable<Edge>.groupByRank(getNodeId: (Edge) -> String): LinkedHashMap<Int, MutableList<Edge>> {
        return groupByTo(LinkedHashMap<Int, MutableList<Edge>>()) { graph.rankById(getNodeId(it))!! }
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
