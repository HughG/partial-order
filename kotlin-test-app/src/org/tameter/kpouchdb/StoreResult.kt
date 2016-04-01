package org.tameter.kpouchdb

/**
* Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
*/

@Suppress("unused")
@native
interface StoreResult {
    val ok: Boolean
    val id: String
    val rev: String
}