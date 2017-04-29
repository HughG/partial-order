package org.tameter.kpouchdb

abstract class PouchDoc(
        var _id: String,
        var type: String
) {
    var rev: String? = null

    override fun toString(): String {
        return "{_id: ${_id}, type: ${type}, rev: ${rev}}"
    }
}