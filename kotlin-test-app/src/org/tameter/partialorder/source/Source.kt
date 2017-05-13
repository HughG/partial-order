package org.tameter.partialorder.source

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB

interface Source {
    fun populate(db: PouchDB) : Promise<PouchDB>
}