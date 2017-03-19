package org.tameter.partialorder.dag

import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.kpouchdb.EdgeDoc

open class Edge(
    doc: EdgeDoc
) : DocWrapper<EdgeDoc>(doc) {
    //    var axis_id: String

    val fromId get() = doc.fromId
    val toId get() = doc.toId

    // NOTE 2016-04-02 HughG: Normally polymorphic equals is wrong because it ends up being
    // non-commutative.  However, in this case it's okay because the base class is abstract (so
    // we'll never get any instances of just that class) and the subclasses don't add any state
    // which is relevant to equality.

    final override fun equals(other: Any?): Boolean{
        if (this === other) return true

        other as EdgeDoc

        if (fromId != other.fromId) return false
        if (toId != other.toId) return false

        return super.equals(other)
    }

    final override fun hashCode(): Int{
        var result = super.hashCode()
        result += 31 * result + fromId.hashCode()
        result += 31 * result + toId.hashCode()
        return result
    }

    override fun toString(): String{
        return "Edge(from ${fromId}, to ${toId}, doc ${doc})"
    }

    override fun toPrettyString(): String {
        return "Edge ${fromId} to ${toId}"
    }
}

fun Edge(
        from: Node,
        to: Node
): Edge {
    return Edge(EdgeDoc(from.doc._id, to.doc._id))
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
        doc.rev = result.rev
        this
    }
}
