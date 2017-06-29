package org.tameter.partialorder.dag

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.kpouchdb.StoreResult
import org.tameter.kpouchdb.remove
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc

class Edge(
        doc: EdgeDoc
) : DocWrapper<EdgeDoc>(doc) {
    //    var axis_id: String

    val graphId get() = doc.graphId
    val fromId get() = doc.fromId
    val toId get() = doc.toId

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Edge) return false

        if (graphId != other.graphId) return false
        if (fromId != other.fromId) return false
        if (toId != other.toId) return false

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = 0
        result += 31 * result + graphId.hashCode()
        result += 31 * result + fromId.hashCode()
        result += 31 * result + toId.hashCode()
        return result
    }

    override fun toString(): String {
        return "Edge(from ${fromId}, to ${toId}, doc ${doc})"
    }

    override fun toPrettyString(): String {
        return "Edge ${fromId} to ${toId}"
    }

    fun reverse() = Edge(graphId, toId, fromId)
}

fun Edge(
        graph: Graph,
        from: Node,
        to: Node
): Edge {
    return Edge(EdgeDoc(graph.id, from.doc._id, to.doc._id))
}

fun Edge(
        graphId: String,
        fromId: String,
        toId: String
): Edge {
    return Edge(EdgeDoc(graphId, fromId, toId))
}

// Copy constructor (also copies the doc)
fun Edge(edge: Edge): Edge {
    return Edge(EdgeDoc(edge.doc))
}

fun Edge.store(
        db: PouchDB
): Promise<Edge> {
    return db.put(doc).thenV { result ->
        if (!result.ok) {
            throw Exception("Failed to store ${doc}")
        }
        // Update rev to match DB, otherwise we won't be able to store any changes later.
        doc._rev = result.rev
        this
    }
}

fun Edge.remove(
        db: PouchDB
): Promise<StoreResult> {
    return db.remove(doc)
}
