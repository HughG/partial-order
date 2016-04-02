package org.tameter.partialorder.dag

import org.tameter.kotlinjs.makeGuid
import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB
import kotlin.reflect.KProperty

class NodeFromDocs() {
    operator fun getValue(thisRef: Edge, property: KProperty<*>): Node {
        val name = property.name
        val id: String = thisRef.doc[name]
        thisRef.graph.nodes.find { it.doc._id == id }
                ?: throw Exception("No '${name}' node ${id}")
    }
//    operator fun setValue(thisRef: Edge, property: KProperty<*>, value: Node) {
//        thisRef.doc[property.name] = value.doc._id
//    }
}

@native("Object")
class Edge(
    graph: Graph,
    doc: EdgeDoc
) : GraphElement<EdgeDoc>(graph, doc) {
//    var axis_id: String
    val from: Node by NodeFromDocs()
    val to: Node by NodeFromDocs()
}

fun Edge(
        graph: Graph,
        db: PouchDB,
        from: Node,
        to: Node
): Promise<Edge> {
    val doc = EdgeDoc(from.doc._id, to.doc._id)
    return db.put(doc).thenV { result ->
        if (!result.ok) {
            throw Exception("Failed to store ${doc}")
        }
        // Update rev to match DB, otherwise we won't be able to store any changes later.
        doc.rev = result.rev
        Edge(graph, doc)
    }
}
