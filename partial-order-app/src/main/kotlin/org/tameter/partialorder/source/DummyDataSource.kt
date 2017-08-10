package org.tameter.partialorder.source

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDB
import org.tameter.partialorder.dag.Node

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
//class DummyDataSource : Source {
//    override val sourceId = "Dummy"
//
//    override fun populate(db: PouchDB) : Promise<PouchDB> {
//        val dummyGraphObjects = (1..20).map { Node("Node_$it").doc }.toTypedArray()
//        console.log("Bulk store inputs:")
//        dummyGraphObjects.forEach {
//            console.log(it)
//        }
//        return db.bulkDocs(dummyGraphObjects).thenV { results ->
//            console.log("Bulk store results:")
//            results.forEach { console.log(it) }
//            db
//        }
//
//        // TODO 2016-04-01 HughG: Should sanity-check for cycles in the graph.
//    }
//}