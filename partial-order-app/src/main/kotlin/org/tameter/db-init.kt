package org.tameter

import org.tameter.kotlin.delegates.setOnce
import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB

class Databases(
        val scoringDatabase: PouchDB,
        val configDatabase: PouchDB
)

internal fun initialiseDatabases(scoringDatabaseName: String, configDatabaseName: String) : Promise<Databases> {
    var scoringDB: PouchDB by setOnce()
    return resetDatabase(scoringDatabaseName).thenP { db ->
        scoringDB = db
        ensureDatabase(configDatabaseName)
    }.thenV { configDB ->
        Databases(scoringDB, configDB)
    }
}

internal fun resetDatabase(databaseName: String): Promise<PouchDB> {
    return (PouchDB(databaseName)).destroy().thenP {
        ensureDatabase(databaseName)
    }
}

private fun ensureDatabase(databaseName: String): Promise<PouchDB> {
    val db: PouchDB = PouchDB(databaseName)

    return db.info().thenV { info ->
        console.log(info)
        db
    }
}
