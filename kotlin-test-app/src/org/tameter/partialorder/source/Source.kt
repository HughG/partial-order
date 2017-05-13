package org.tameter.partialorder.source

import org.tameter.kpouchdb.PouchDB
import kotlin.js.Promise

interface Source {
    fun populate(db: PouchDB) : Promise<PouchDB>
}