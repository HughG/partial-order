package org.tameter.partialorder.dag

import org.tameter.kpouchdb.PouchDoc
import org.tameter.partialorder.dag.kpouchdb.GraphElementDoc

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

abstract class DocWrapper<TDoc: GraphElementDoc>(
        internal val doc: TDoc
) : PouchDoc by doc {
    abstract fun toPrettyString(): String

    override fun equals(other: Any?): Boolean{
        if (this === other) return true

        if (other !is DocWrapper<*>) return false

        if (_id != other._id) return false
        if (type != other.type) return false
        if (rev != other.rev) return false

        return true
    }

    override fun hashCode(): Int{
        var result = _id.hashCode()
        result += 31 * result + type.hashCode()
        result += 31 * result + (rev ?: "").hashCode()
        return result
    }
}