package org.tameter.kotlin.js.promise

import org.tameter.kotlin.js.logError

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

/**
    See very useful info about (newbie mistakes with) promises at

    https://pouchdb.com/2015/05/18/we-have-a-problem-with-promises.html

    TODO 2016-04-01 HughG: Add @CheckReturnValue from FindBugs / JSR 305
 */
external class Promise<out T> : kotlin.js.Promise<T> {
    @JsName("then") fun <U> thenV(result: (T) -> U): Promise<U>
    @JsName("then") fun <U> thenP(result: (T) -> kotlin.js.Promise<U>): Promise<U>
}

fun <T> Promise<T>.catchAndLog(): Unit {
    catch(::logError)
}
