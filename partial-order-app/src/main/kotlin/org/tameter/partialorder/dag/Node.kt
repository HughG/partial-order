package org.tameter.partialorder.dag

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.kpouchdb.toStringForExternal
import org.tameter.partialorder.dag.kpouchdb.NodeDoc
import org.tameter.partialorder.util.makeGuid

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

class Node(
        doc: NodeDoc
) : DocWrapper<NodeDoc>(doc) {
    val source get() = "${doc.sourceDescription}: ${doc.index}"
    val description get() = doc.description

    override fun equals(other: Any?): Boolean{
        if (this === other) return true

        if (other !is Node) return false

        if (source != other.source) return false
        if (description != other.description) return false

        return super.equals(other)
    }

    override fun hashCode(): Int{
        var result = super.hashCode()
        result += 31 * result + source.hashCode()
        result += 31 * result + description.hashCode()
        return result
    }

    override fun toString(): String{
        return "Node(doc ${doc.toStringForExternal()})"
    }

    override fun toPrettyString(): String {
        return "Node ${source}: ${description}"
    }
}

// Copy constructor (also copies the doc)
fun Node(node: Node): Node {
    return Node(NodeDoc(node.doc))
}

fun Node.store(
        db: PouchDB
): Promise<Node> {
    return db.put(doc).thenV { result ->
        if (!result.ok) {
            throw Exception("Failed to store ${doc}")
        }
        // Update rev to match DB, otherwise we won't be able to store any changes later.
        doc._rev = result.rev
        this
    }
}
