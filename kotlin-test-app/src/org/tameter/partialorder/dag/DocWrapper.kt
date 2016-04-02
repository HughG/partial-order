package org.tameter.partialorder.dag

import org.tameter.kotlinjs.JSMapDelegate
import org.tameter.partialorder.dag.kpouchdb.GraphElementDoc

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

abstract class DocWrapper<TDoc: GraphElementDoc>(
        internal val doc: TDoc
) {
    var _id: String by JSMapDelegate(doc)
    var type: String by JSMapDelegate(doc)
    var rev: String by JSMapDelegate(doc)

    abstract fun toPrettyString(): String

    override fun equals(other: Any?): Boolean{
        if (this === other) return true

        other as DocWrapper<*>

        if (_id != other._id) return false
        if (type != other.type) return false
        if (rev != other.rev) return false

        return true
    }

    override fun hashCode(): Int{
        var result = _id.hashCode()
        result += 31 * result + type.hashCode()
        result += 31 * result + rev.hashCode()
        return result
    }
}