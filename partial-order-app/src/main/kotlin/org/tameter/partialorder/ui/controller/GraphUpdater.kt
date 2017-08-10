package org.tameter.partialorder.ui.controller

import org.tameter.kotlin.browser.setTimeoutLoggingErrors
import org.tameter.kotlin.js.doOrLogError
import org.tameter.kpouchdb.Change
import org.tameter.kpouchdb.PouchDB
import org.tameter.kpouchdb.toStringForExternal
import org.tameter.partialorder.dag.CompositeScoring
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Graph
import org.tameter.partialorder.dag.Node
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.ui.view.AppUI
import kotlin.browser.window

class GraphUpdater(val db: PouchDB, val graphs: CompositeScoring) {
    init {
        AppUI.activeTabProperty.onNext { ensureRender() }
    }

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
                    graphs.addNode(graphNode)
                }
            }
            "E" -> {
                val edge = Edge(doc.unsafeCast<EdgeDoc>())
                console.log(edge.toPrettyString())
                val graphId = edge.graphId
                val graph = graphs.findScoringById(graphId)
                when (graph) {
                    is Graph -> {
                        if (change.deleted) {
                            graph.removeEdge(edge)
                        } else {
                            graph.addEdge(edge)
                        }
                    }
                    null -> console.error("Unknown graph $graphId")
                    else -> console.error("Scoring $graphId exists but is not a graph; cannot add $edge")
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
            window.setTimeoutLoggingErrors {
                needsRender = false
                AppUI.render(db, graphs)
            }
        }
    }
}