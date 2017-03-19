package org.tameter.kpouchdb

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

@Suppress("unused")
external interface BulkQueryRowValue {
    val rev: String
    val deleted: Boolean?
}