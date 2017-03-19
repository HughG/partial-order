package org.tameter.kpouchdb

/**
* Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
*/

@Suppress("unused")
external interface StoreResult {
    val ok: Boolean
    val id: String
    val rev: String
}