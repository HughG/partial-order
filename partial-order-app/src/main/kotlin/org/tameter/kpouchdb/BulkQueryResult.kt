package org.tameter.kpouchdb

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@Suppress("unused")
external interface BulkQueryResult<T> {
    val total_rows: Int
    val offset: Int
    val rows: Array<BulkQueryRow<T>>
}