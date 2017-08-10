package org.tameter.partialorder.source

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB

interface Source {
    // TODO 2017-08-10 HughG: Document this!
    val sourceId: String
    fun populate(db: PouchDB) : Promise<PouchDB>
}