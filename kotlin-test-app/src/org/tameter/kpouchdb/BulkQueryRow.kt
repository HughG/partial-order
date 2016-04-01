package org.tameter.kpouchdb

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@Suppress("unused")
@native
interface BulkQueryRow<T> {
    val id: String
    val key: String
    val value: BulkQueryRowValue
    val doc: T?
}