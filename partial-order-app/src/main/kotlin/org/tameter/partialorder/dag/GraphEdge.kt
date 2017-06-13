package org.tameter.partialorder.dag

import org.tameter.kpouchdb.toStringForExternal
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc

class GraphEdge(
        val graph: Graph,
        doc: EdgeDoc
) : Edge(doc) {
    //    var axis_id: String
    val from get() = nodeFromGraph("from", doc.fromId)
    val to get() = nodeFromGraph("to", doc.toId)

    init {
        // Sanity-check existence of from/to nodes by referencing properties
        from
        to
    }

    override fun toString(): String{
        return "GraphEdge(from ${from}, to ${to}, doc ${doc.toStringForExternal()})"
    }

    override fun toPrettyString(): String {
        return "GraphEdge ${from.toPrettyString()} to ${to.toPrettyString()}"
    }

    private fun nodeFromGraph(nodeType: String, nodeId: String): GraphNode {
        return graph.findNodeById(nodeId) ?: throw Exception("No '${nodeType}' node ${nodeId}")
    }
}

fun GraphEdge(
        graph: Graph,
        edge: Edge
): GraphEdge {
    return GraphEdge(graph, EdgeDoc(edge.fromId, edge.toId))
}

fun GraphEdge(
        graph: Graph,
        from: Node,
        to: Node
): Edge {
    return GraphEdge(graph, EdgeDoc(from.doc._id, to.doc._id))
}

// Copy constructor (also copies the doc)
fun GraphEdge(graph: Graph, edge: GraphEdge): GraphEdge {
    return GraphEdge(graph, EdgeDoc(edge.doc))
}
