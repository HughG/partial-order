package org.tameter

import org.tameter.kotlinjs.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Edge
import org.tameter.partialorder.dag.Node

private val DB_NAME = "http://localhost:5984/ranking"

internal fun initDB(): Promise<PouchDB> {
    return resetDB().thenV { db ->
        addDummyData(db)
        db
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

private fun addDummyData(db: PouchDB): Promise<dynamic> {
    var readNode = Node("read").apply {
        description = "Investigate stuff";
    }
    var sighNode = Node("sigh").apply {
        description = "Be frustrated at difficulty of new stuff";
    }
    var grumpNode = Node("grump").apply {
        description = "Grumble to self about difficulty of new stuff";
    }
    var edge1 = Edge(readNode, sighNode).apply {
        axis_id = "Dependency";
    }
    return db.bulkDocs(arrayOf(
        readNode,
        sighNode,
        grumpNode,
        edge1
    )).thenV { results ->
        console.log("Bulk store results:")
        results.forEach { console.log(it) }
    }
}