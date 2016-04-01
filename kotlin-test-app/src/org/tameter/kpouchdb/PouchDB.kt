package org.tameter.kpouchdb

import org.tameter.kotlinjs.JSMap
import org.tameter.kotlinjs.promise.Promise

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@Suppress("unused")
@native
open class PouchDB(var name: String, var options: JSMap<dynamic> = JSMap())
{
    // Delete database
    fun destroy(options: JSMap<dynamic> = JSMap()): Promise<dynamic> = noImpl

    // Create/update doc
    fun put(doc: dynamic): Promise<StoreResult> = noImpl
    fun get(id: String): Promise<dynamic> = noImpl

    // Batch create
    fun bulkDocs(docs: Array<Any>, options: JSMap<dynamic> = JSMap()): Promise<Array<StoreResult>> = noImpl

    // Batch fetch
    fun <T> allDocs(options: JSMap<dynamic> = JSMap()): Promise<BulkQueryResult<T>> = noImpl

    // Database info
    fun info(): Promise<dynamic> = noImpl
}
