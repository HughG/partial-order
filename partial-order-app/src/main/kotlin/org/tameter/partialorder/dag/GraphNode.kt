package org.tameter.partialorder.dag

import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.util.makeGuid

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class GraphNode(
        val graph: Graph,
        doc: NodeDoc
) : Node(doc) {
    fun outgoing(): Set<GraphEdge> {
        return graph.edges.filter { it.from == this }.toSet()
    }
}

fun GraphNode(
        graph: Graph,
        description: String
): GraphNode {
    return GraphNode(graph, NodeDoc(makeGuid(), description))
}


fun GraphNode(
        graph: Graph,
        node: Node
): GraphNode {
    return GraphNode(graph, NodeDoc(node.doc))
}

// Copy constructor (also copies the doc)
fun GraphNode(graph: Graph, node: GraphNode): GraphNode {
    return GraphNode(graph, NodeDoc(node.doc))
}
