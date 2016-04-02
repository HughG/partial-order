package org.tameter.partialorder.dag

import org.tameter.partialorder.dag.kpouchdb.EdgeDoc
import kotlin.reflect.KProperty

class NodeFromGraph() {
    operator fun getValue(thisRef: GraphEdge, property: KProperty<*>): GraphNode {
        val name = property.name
        val id: String = thisRef.doc[name + "Id"]
        return thisRef.graph.nodes.find { it._id == id }
                ?: throw Exception("No '${name}' node ${id}")
    }

    operator fun setValue(thisRef: Edge, property: KProperty<*>, value: Node) {
        thisRef.doc[property.name + "Id"] = value.doc._id
    }
}

class GraphEdge(
        val graph: Graph,
        doc: EdgeDoc
) : Edge(doc) {
    //    var axis_id: String
    val from: GraphNode by NodeFromGraph()
    val to: GraphNode by NodeFromGraph()

    init {
        // Sanity-check existence of from/to nodes by referencing properties
        from
        to
        // Safe to add to the graph, now that we know the nodes exist.
        graph.edges.add(this)
        // TODO 2016-04-02 HughG: Not properly safe, because someone could remove nodes later.
    }

    override fun toString(): String{
        return "GraphEdge(from ${from}, to ${to}, doc ${doc.toString()})"
    }

    override fun toPrettyString(): String {
        return "GraphEdge ${from.toPrettyString()} to ${to.toPrettyString()}"
    }
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
