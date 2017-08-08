package org.tameter.partialorder.source.kpouchdb

import org.tameter.kpouchdb.PouchDoc

val SOURCE_SPEC_DOC_TYPE: String = "Spec"

external interface SourceSpecDoc : PouchDoc {
    val description: String
}

fun <T: SourceSpecDoc> SourceSpecDoc(_id: String, type: String, description: String): T {
    return PouchDoc<T>(_id, type).apply {
        asDynamic().description = description
    }
}
