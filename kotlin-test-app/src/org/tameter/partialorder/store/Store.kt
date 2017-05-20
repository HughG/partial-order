package org.tameter.partialorder.store

import org.tameter.kotlin.js.promise.Promise
import org.tameter.kpouchdb.PouchDoc

/**
 * Copyright (c) 2017 Hugh Greene (githugh@tameter.org).
 */
interface Store<T: PouchDoc> {
    fun <S: T> store(doc: S): Promise<S>
}