package org.tameter.kpouchdb

import org.tameter.kotlin.js.promise.Promise

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@Suppress("unused")
external open class PouchDB(name: String, options: PouchDBOptions = definedExternally) {
    var name: String

    // Delete database
    fun destroy(/*options: JSMap<dynamic> = definedExternally*/): Promise<dynamic>

    // Create/update doc
    fun put(doc: PouchDoc): Promise<StoreResult>
    fun get(id: String): Promise<PouchDoc>

    // Batch create
    fun bulkDocs(docs: Array<out PouchDoc>/*, options: JSMap<dynamic> = definedExternally*/): Promise<Array<StoreResult>>

    // Batch fetch
    fun <T> allDocs(options: AllDocsOptions = definedExternally): Promise<BulkQueryResult<T>>

    // Database info
    fun info(): Promise<dynamic>

    @JsName("changes")
    fun allChanges(options: ChangeOptions): Promise<Changes>

    fun changes(options: ChangeOptions): ChangeFeed
}

fun PouchDB.liveChanges(options: ChangeOptions): ChangeFeed {
    options.live = true
    return changes(options)
}

external interface PouchDBOptions
fun PouchDBOptions(): PouchDBOptions = js("{ return {}; }")

external interface AllDocsOptions {
    var startkey : String
    var endkey: String
    var include_docs: Boolean
}
fun AllDocsOptions(): AllDocsOptions = js("{ return {}; }")

external interface ChangeOptions {
    var since: dynamic /* Int | String */
    var live: Boolean
    var include_docs: Boolean
    var limit: Int
}
fun ChangeOptions(): ChangeOptions = js("{ return {}; }")
fun ChangeOptions.sinceNow() { this.since = "now" }
fun ChangeOptions.sinceSeq(seq : Int) { this.since = seq }

external interface Change {
    val id: String
    val deleted: Boolean
    val doc: PouchDoc?
}

external interface Changes {
    val results: Array<Change>
}

external interface ChangeFeed {
    fun <T> on(event: String, handler: (T) -> Unit): ChangeFeed
}

fun ChangeFeed.onChange(handler: (Change) -> Unit) { this.on("change", handler) }
fun ChangeFeed.onError(handler: (Throwable) -> Unit) { this.on("change", handler) }
