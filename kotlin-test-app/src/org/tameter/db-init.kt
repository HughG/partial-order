package org.tameter

import org.tameter.kotlin.js.promise.async
import org.tameter.kotlin.js.promise.await
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.source.GitHubSource
import kotlin.js.Promise

private val DB_NAME = "http://localhost:5984/ranking"

internal fun initDB(): Promise<PouchDB> = async {
    val db = resetDB().await()
//    DummyDataSource().populate(db)
    GitHubSource("HughG", "partial-order").populate(db).await()
}

private fun resetDB(): Promise<PouchDB> = async {
    (PouchDB(DB_NAME)).destroy().await()

    val db: PouchDB = PouchDB(DB_NAME)
    console.log(db.info().await())
    db
}
