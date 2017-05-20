package org.tameter

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.source.GitHubSource

private val DB_NAME = "http://localhost:5984/ranking"

internal fun initDB(): Promise<PouchDB> {
    return resetDB().thenP { db ->
//        DummyDataSource().populate(db)
        GitHubSource("HughG", "partial-order").populate(db)
    }
}

private fun resetDB(): Promise<PouchDB> {
    return (PouchDB(DB_NAME)).destroy().thenP {
        val db: PouchDB = PouchDB(DB_NAME)

        db.info().thenV {
            console.log(it)
            db
        }
    }
}
