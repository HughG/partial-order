package org.tameter.partialorder.ui.controller

import org.tameter.kotlin.js.doOrLogError
import org.tameter.kpouchdb.Change
import org.tameter.kpouchdb.PouchDB
import org.tameter.kpouchdb.toStringForExternal
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.MultiGraph
import org.tameter.partialorder.dag.Node
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.ui.view.render
import kotlin.browser.window

class GraphUpdater(val db: PouchDB, val graphs: MultiGraph) {
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
                val graphEdge = Edge(doc.unsafeCast<EdgeDoc>())
                console.log(graphEdge.toPrettyString())
                val graphId = graphEdge.graphId
                val graph = graphs.findGraphById(graphId)
                if (graph == null) {
                    console.error("Unknown graph $graphId")
                } else {
                    if (change.deleted) {
                        graph.removeEdge(graphEdge)
                    } else {
                        graph.addEdge(graphEdge)
                    }
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
                render(db, graphs)
            }, 0)
        }
    }
}