package org.tameter

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB

private val DB_NAME = "http://localhost:5984/ranking"

internal fun resetDB(): Promise<PouchDB> {
    return (PouchDB(DB_NAME)).destroy().thenP {
        val db: PouchDB = PouchDB(DB_NAME)

        db.info().thenV { info ->
            console.log(info)
            db
        }
    }
}
