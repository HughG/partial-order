package org.tameter.partialorder.dag

import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB
import kotlin.reflect.KProperty

class NodeFromDocs() {
    operator fun getValue(thisRef: Edge, property: KProperty<*>): Node {
        val name = property.name
        val id: String = thisRef.doc[name]
        return thisRef.graph.nodes.find { it._id == id }
                ?: throw Exception("No '${name}' node ${id}")
    }
//    operator fun setValue(thisRef: Edge, property: KProperty<*>, value: Node) {
//        thisRef.doc[property.name] = value.doc._id
//    }
}

class Edge(
    graph: Graph,
    doc: EdgeDoc
) : GraphElement<EdgeDoc>(graph, doc) {
//    var axis_id: String
    val from: Node by NodeFromDocs()
    val to: Node by NodeFromDocs()

    init {
        graph.edges.add(this)
    }

    override fun toString(): String{
        return "Edge(from ${from}, to ${to}, doc ${doc.toString()})"
    }
}

fun Edge(
        graph: Graph,
        from: Node,
        to: Node
): Edge {
    val doc = EdgeDoc(from.doc._id, to.doc._id)
    val edge = Edge(graph, doc)
//    console.log(edge.toString())
    return edge
}

fun Edge.store(
        db: PouchDB
): Promise<Edge> {
    return db.put(doc).thenV { result ->
        if (!result.ok) {
            throw Exception("Failed to store ${doc}")
        }
        // Update rev to match DB, otherwise we won't be able to store any changes later.
        doc.rev = result.rev
        this
    }
}
