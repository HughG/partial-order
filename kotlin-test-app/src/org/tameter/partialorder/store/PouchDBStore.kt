package org.tameter.partialorder.store

import org.tameter.kotlin.js.promise.async
import org.tameter.kotlin.js.promise.await
import org.tameter.kpouchdb.PouchDB
import org.tameter.kpouchdb.PouchDoc
import kotlin.js.Promise

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
class PouchDBStore(
        val db: PouchDB
) : Store<PouchDoc> {
    override fun <S: PouchDoc> store(doc: S): Promise<S> = async {
        val result = db.put(doc).await()
        if (!result.ok) {
            throw Exception("Failed to store ${doc}")
        }
        // Update rev to match DB, otherwise we won't be able to store any changes later.
        doc._rev = result.rev
        doc
    }
}