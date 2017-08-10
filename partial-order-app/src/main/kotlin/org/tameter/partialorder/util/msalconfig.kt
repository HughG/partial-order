package org.tameter.partialorder.util

import org.w3c.dom.Location
import kotlin.browser.window

class MsalConfig(val clientID: String, val redirectUri: String)

private fun Location.withoutQuery(): String {
    return "${protocol}//${host}${pathname}${hash}"
}

// Azure app registration portal URL is https://apps.dev.microsoft.com/#/application/5e278f50-c6bf-4594-a8cc-6cb1439761de
val msalconfig = MsalConfig(
        clientID = "5e278f50-c6bf-4594-a8cc-6cb1439761de",
        redirectUri = window.location.withoutQuery() // Strip query part added by IntelliJ debugger launch.
)