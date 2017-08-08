package org.tameter.kotlin.browser

import org.tameter.kotlin.js.doOrLogError
import org.w3c.dom.WindowOrWorkerGlobalScope

inline fun WindowOrWorkerGlobalScope.setTimeoutLoggingErrors(
        crossinline handler: (Array<out Any?>) -> Unit,
        timeout: Int = 0,
        vararg arguments: Any?
) {
    this.setTimeout({ doOrLogError { handler(arguments) } }, timeout)
}

inline fun WindowOrWorkerGlobalScope.setTimeoutLoggingErrors(
        timeout: Int = 0,
        vararg arguments: Any?,
        crossinline handler: (Array<out Any?>) -> Unit
) {
    this.setTimeout({ doOrLogError { handler(arguments) } }, timeout)
}
