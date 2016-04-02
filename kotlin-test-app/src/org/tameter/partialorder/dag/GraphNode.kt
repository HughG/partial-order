package org.tameter.partialorder.dag

import org.tameter.kotlinjs.makeGuid
import org.tameter.partialorder.dag.kpouchdb.NodeDoc

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class GraphNode(
        val graph: Graph,
        doc: NodeDoc
) : Node(doc) {
    init {
        graph.nodes.add(this)
    }

    fun outgoing(): Set<GraphEdge> {
        return graph.edges.filter { it.from == this }.toSet()
    }
}

fun GraphNode(
        graph: Graph,
        description: String
): GraphNode {
    return GraphNode(graph, NodeDoc(makeGuid()).apply {
        this.description = description
    })
}

// Copy constructor (also copies the doc)
fun GraphNode(graph: Graph, node: GraphNode): GraphNode {
    return GraphNode(graph, NodeDoc(node.doc))
}
