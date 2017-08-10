package org.tameter.partialorder.util

/**
 * NOTE 2017-08-10 HughG: Adapted from https://github.com/Azure-Samples/active-directory-javascript-graphapi-v2/archive/master.zip
 *
 * To get this to work, you need to
 *
 *   - register an Office app at https://apps.dev.microsoft.com/;
 *
 *   - set up the app's redirect URL (in the online config) to be "http://localhost:???/???" where the question marks
 *     are filled in according to what you get when you ask IntelliJ to launch
 *     "...\partial-order\partial-order-app\web\index.html" in a browser (or the appropriate online URL if you host it,
 *     in which case it must be "https:...").
 *
 *   - put the registered app GUID into MsalConfig.kt;
 *
 * Currently this just signs in to Office 365 and fetches the user's information.  I wanted to extend it to fetch tasks
 * from an on-premise Exchange server.  Unfortunately the Exchange server I want to target turns out to be running
 * Exchange 2010, and the REST API is only supported from Exchange 2016 CU3, according to comments at
 * https://stackoverflow.com/questions/42413743/does-exchange-server-in-house-suppport-rest-api.  (The comments point to
 * https://blogs.technet.microsoft.com/exchange/2016/09/26/on-premises-architectural-requirements-for-the-rest-api/
 * for details on setting up on-premise Exchange for that.)
 *
 * The original StackOverflow question above says there's a SOAP API (for Exchange 2013):
 * https://msdn.microsoft.com/en-us/library/office/dd877012(v=exchg.150).aspx
 *
 * A little digging found the equivalent doc for Exchange 2010:
 * https://msdn.microsoft.com/en-us/library/office/dd877012(v=exchg.140).aspx
 *
 * The disadvantage of using the SOAP API is that you have to explicitly handle/store user credentials somewhere
 * yourself.
 */

import org.tameter.kotlin.js.jsobject
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import kotlin.browser.document
import kotlin.browser.window

var graphAPIMeEndpoint = "https://graph.microsoft.com/v1.0/me"
var graphAPIScopes = arrayOf("https://graph.microsoft.com/user.read")

// Initialize application
var userAgentApplication = Msal.UserAgentApplication(msalconfig.clientID).apply { redirectUri = msalconfig.redirectUri }

/*
 * NOTE 2017-08-10 HughG: Could probably get rid of this and just add an overload of JSON.stringify which accepts null
 * as the replacer argument, by subclassing kotlin.js.JSON.
 */
fun keepEverythingReplacer(@Suppress("UNUSED_PARAMETER") k: String, v: Any?) = v

fun setUpOfficeSignIn() {
    (document.getElementById("callGraphButton")!! as HTMLElement).onclick = { callGraphAPI() }
    (document.getElementById("signOutButton")!! as HTMLElement).onclick = { signOut() }

    displayUserInfo()
}

fun displayUserInfo() {
    val user = userAgentApplication.getUser()
    if (user != null) {
        // Display the user info
        val userInfoElement = document.getElementById("userInfo")!!
        userInfoElement.parentElement!!.classList.remove("hidden")
        userInfoElement.innerHTML = JSON.stringify(user, ::keepEverythingReplacer, 4)

        // Show Sign-Out button
        document.getElementById("signOutButton")!!.classList.remove("hidden")
    }
}

fun callGraphAPI() {
    if (userAgentApplication.getAllUsers().isEmpty()) {
        userAgentApplication.loginPopup()
                .then({ token ->
                    console.log(token)
                    displayUserInfo()
                    callGraphAPI()
                }, { error ->
                    showError("login", error, document.getElementById("errorMessage")!!)
                })
    } else {
        val responseElement = document.getElementById("graphResponse")!! as HTMLElement
        responseElement.parentElement!!.classList.remove("hidden")
        responseElement.innerText = "Calling Graph ..."
        callWebApiWithScope(graphAPIMeEndpoint,
                graphAPIScopes,
                responseElement,
                document.getElementById("errorMessage")!!,
                document.getElementById("accessToken")!!)
    }
}

fun callWebApiWithScope(
        endpoint: String,
        scope: Array<String>,
        responseElement: Element,
        errorElement: Element,
        showTokenElement: Element) {
    userAgentApplication.acquireTokenSilent(scope)
            .then({ token ->
                callWebApiWithToken(endpoint, token, responseElement, errorElement, showTokenElement)
            }, { error ->
                if (error.message?.indexOf("interaction_required") != -1) {
                    userAgentApplication.acquireTokenPopup(scope).then({ token ->
                        callWebApiWithToken(endpoint, token, responseElement, errorElement, showTokenElement)
                    }, { error2 ->
                        showError(endpoint, error2, errorElement)
                    })
                } else {
                    showError(endpoint, error, errorElement)
                }
            })
}

fun showAPIResponse(data: Any?, token: String, responseElement: Element, showTokenElement: Element?) {
    console.log(data)
    responseElement.innerHTML = JSON.stringify(data, ::keepEverythingReplacer, 4)
    if (showTokenElement != null) {
        showTokenElement.parentElement!!.classList.remove("hidden")
        showTokenElement.innerHTML = token
    }
}

fun showError(endpoint: String, error: Any?, errorElement: Element) {
    console.error(error)
    var formattedError = JSON.stringify(error, ::keepEverythingReplacer, 4)
    if (formattedError.length < 3) {
        formattedError = error.toString()
    }
    errorElement.innerHTML = "Error calling $endpoint: $formattedError"
}

fun callWebApiWithToken(
        endpoint: String,
        token: String,
        responseElement: Element,
        errorElement: Element,
        showTokenElement: Element
) {
    val options = jsobject<RequestInit> {
        method = "GET"
        headers = Headers().apply {
            append("Authorization", "Bearer " + token)
        }
    }

    // Note that fetch API is not available in all browsers
    window.fetch(endpoint, options)
            .then({ response ->
                val contentType = response.headers.get("content-type")
                if (response.status == 200.toShort() && contentType != null && contentType.indexOf("application/json") != -1) {
                    response.json()
                            .then({ data ->
                                // Display response in the page
                                showAPIResponse(data, token, responseElement, showTokenElement)
                            })
                            .catch({ error ->
                                showError(endpoint, error, errorElement)
                            })
                } else {
                    response.json()
                            .then({ data ->
                                // Display response in the page
                                showError(endpoint, data, errorElement)
                            })
                            .catch({ error ->
                                showError(endpoint, error, errorElement)
                            })
                }
            })
            .catch({ error ->
                showError(endpoint, error, errorElement)
            })
}

fun signOut() {
    userAgentApplication.logout()
}
