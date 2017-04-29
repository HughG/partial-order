package org.tameter.kotlinjs.promise

/**
 * Copyright (c) 2016 Hugh Greene (githugh@tameter.org).
 */

/**
    See very useful info about (newbie mistakes with) promises at

    https://pouchdb.com/2015/05/18/we-have-a-problem-with-promises.html

    TODO 2016-04-01 HughG: Add @CheckReturnValue from FundBugs / JSR 305
 */
external abstract class Promise<T> {
    @JsName("then") fun <U> thenV(result: (T) -> U): Promise<U>
    @JsName("then") fun <U> thenP(result: (T) -> Promise<U>): Promise<U>
    fun catch(error: (Any) -> Unit): Unit
}

fun <T> Promise<T>.catchAndLog(): Unit {
    catch { console.log(it) }
}