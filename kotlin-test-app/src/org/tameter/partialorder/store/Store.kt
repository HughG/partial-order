package org.tameter.partialorder.store

import org.tameter.kpouchdb.PouchDoc
import kotlin.js.Promise

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
interface Store<T: PouchDoc> {
    fun <S: T> store(doc: S): Promise<S>
}