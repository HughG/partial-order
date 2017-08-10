/**
 * NOTE 2017-08-10 HughG: Adapted from running ts2kt on msal.ts.d in npm module "msal@0.1.1".
 */

@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

import Msal.LogLevel
import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

val Window.msal: Msal.UserAgentApplication
    get() = this["msal"].unsafeCast<Msal.UserAgentApplication>()

typealias TokenReceivedCallback =
    (errorDesc: String, token: String, error: String, tokenType: String) -> Unit

val Window.callBackMappedToRenewStates: MutableMap<String, TokenReceivedCallback>
    get() = this["callBackMappedToRenewStates"].unsafeCast<MutableMap<String, TokenReceivedCallback>>()

external interface PromiseCallbacks {
    val resolve: (Any) -> Unit
    val reject: (Throwable) -> Unit
}

val Window.callBacksMappedToRenewStates: MutableList<PromiseCallbacks>
    get() = this["callBacksMappedToRenewStates"].unsafeCast<MutableList<PromiseCallbacks>>()

typealias ILoggerCallback =
    (level: LogLevel, message: String, containsPii: Boolean) -> Unit
