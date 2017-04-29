package org.tameter.kpouchdb

abstract class PouchDoc(
        var _id: String,
        var type: String
) {
    // TODO 2017-04-29 HughG: This should be _rev, but then it's passed as null on new documents, which is rejected
    // as an illegal format.  Using an empty string doesn't work, either.  Maybe this should be an external class,
    // with a "constructor" which makes a new empty JS object and sets the properties?
    var rev: String? = null

    override fun toString(): String {
        return "{_id: ${_id}, type: ${type}, rev: ${rev}}"
    }
}