/**
 * NOTE 2017-08-10 HughG: Adapted from running ts2kt on msal.ts.d in npm module "msal@0.1.1".
 */

@file:Suppress(
        "INTERFACE_WITH_SUPERCLASS",
        "OVERRIDING_FINAL_MEMBER",
        "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
        "CONFLICTING_OVERLOADS",
        "unused"
)
@file:JsQualifier("Msal")
package Msal

import ILoggerCallback
import kotlin.js.Promise

external enum class AuthorityType {
    Aad /* = 0 */,
    Adfs /* = 1 */,
    B2C /* = 2 */
}
external open class Authority(authority: String, validateAuthority: Boolean) {
    open var AuthorityType: AuthorityType = definedExternally
    open var IsValidationEnabled: Boolean = definedExternally
    open var Tenant: String = definedExternally
    open var tenantDiscoveryResponse: Any = definedExternally
    open var AuthorizationEndpoint: String = definedExternally
    open var EndSessionEndpoint: String = definedExternally
    open var SelfSignedJwtAudience: String = definedExternally
    open fun validateResolved(): Unit = definedExternally
    open var CanonicalAuthority: String = definedExternally
    open var canonicalAuthority: Any = definedExternally
    open var canonicalAuthorityUrlComponents: Any = definedExternally
    open var CanonicalAuthorityUrlComponents: IUri = definedExternally
    open var DefaultOpenIdConfigurationEndpoint: String = definedExternally
    open fun validateAsUri(): Unit = definedExternally
    open fun DiscoverEndpoints(openIdConfigurationEndpoint: Any): Unit = definedExternally
    open fun ResolveEndpointsAsync(): Promise<Authority> = definedExternally
    open fun GetOpenIdConfigurationEndpointAsync(): Promise<String> = definedExternally
    companion object {
        fun DetectAuthorityFromUrl(authorityUrl: Any): Unit = definedExternally
        fun CreateInstance(authorityUrl: String, validateAuthority: Boolean): Authority = definedExternally
    }
}
external open class AadAuthority(authority: String, validateAuthority: Boolean) : Authority {
    open var AadInstanceDiscoveryEndpointUrl: Any = definedExternally
    override var AuthorityType: AuthorityType = definedExternally
    override fun GetOpenIdConfigurationEndpointAsync(): Promise<String> = definedExternally
    open fun IsInTrustedHostList(host: String): Boolean = definedExternally
    companion object {
        var AadInstanceDiscoveryEndpoint: Any = definedExternally
        var TrustedHostList: Any = definedExternally
    }
}
external open class AccessTokenCacheItem(key: AccessTokenKey, value: AccessTokenValue) {
    open var key: AccessTokenKey = definedExternally
    open var value: AccessTokenValue = definedExternally
}
external open class AccessTokenKey(authority: String, clientId: String, scopes: String, uid: String, utid: String) {
    open var authority: String = definedExternally
    open var clientId: String = definedExternally
    open var userIdentifier: String = definedExternally
    open var scopes: String = definedExternally
}
external open class AccessTokenValue(accessToken: String, idToken: String, expiresIn: String, clientInfo: String) {
    open var accessToken: String = definedExternally
    open var idToken: String = definedExternally
    open var expiresIn: String = definedExternally
    open var clientInfo: String = definedExternally
}
external open class AuthenticationRequestParameters(authority: Authority, clientId: String, scope: Array<String>, responseType: String, redirectUri: String) {
    open var authorityInstance: Authority = definedExternally
    open var clientId: String = definedExternally
    open var nonce: String = definedExternally
    open var state: String = definedExternally
    open var correlationId: String = definedExternally
    open var xClientVer: String = definedExternally
    open var xClientSku: String = definedExternally
    open var scopes: Array<String> = definedExternally
    open var responseType: String = definedExternally
    open var promptValue: String = definedExternally
    open var extraQueryParameters: String = definedExternally
    open var loginHint: String = definedExternally
    open var domainHint: String = definedExternally
    open var redirectUri: String = definedExternally
    open var authority: String = definedExternally
    open fun createNavigateUrl(scopes: Array<String>): String = definedExternally
    open fun translateclientIdUsedInScope(scopes: Array<String>): Unit = definedExternally
    open fun parseScope(scopes: Array<String>): String = definedExternally
}
external open class B2cAuthority(authority: String, validateAuthority: Boolean) : AadAuthority {
    override var AuthorityType: AuthorityType = definedExternally
    override fun GetOpenIdConfigurationEndpointAsync(): Promise<String> = definedExternally
}
external open class ClientInfo(rawClientInfo: String) {
    open var _uid: Any = definedExternally
    open var uid: String = definedExternally
    open var _utid: Any = definedExternally
    open var utid: String = definedExternally
}
external open class Constants {
    companion object {
        var errorDescription: String = definedExternally
        var error: String = definedExternally
        var scope: String = definedExternally
        var acquireTokenUser: String = definedExternally
        var clientInfo: String = definedExternally
        var clientId: String = definedExternally
        var authority: String = definedExternally
        var idToken: String = definedExternally
        var accessToken: String = definedExternally
        var expiresIn: String = definedExternally
        var sessionState: String = definedExternally
        var tokenKeys: String = definedExternally
        var accessTokenKey: String = definedExternally
        var expirationKey: String = definedExternally
        var stateLogin: String = definedExternally
        var stateAcquireToken: String = definedExternally
        var stateRenew: String = definedExternally
        var nonceIdToken: String = definedExternally
        var userName: String = definedExternally
        var idTokenKey: String = definedExternally
        var loginRequest: String = definedExternally
        var loginError: String = definedExternally
        var renewStatus: String = definedExternally
        var resourceDelimeter: String = definedExternally
        var _loadFrameTimeout: Any = definedExternally
        var loadFrameTimeout: Number = definedExternally
        var tokenRenewStatusCancelled: String = definedExternally
        var tokenRenewStatusCompleted: String = definedExternally
        var tokenRenewStatusInProgress: String = definedExternally
        var _popUpWidth: Any = definedExternally
        var popUpWidth: Number = definedExternally
        var _popUpHeight: Any = definedExternally
        var popUpHeight: Number = definedExternally
        var login: String = definedExternally
        var renewToken: String = definedExternally
        var unknown: String = definedExternally
    }
}
external open class ErrorCodes {
    companion object {
        var loginProgressError: String = definedExternally
        var acquireTokenProgressError: String = definedExternally
        var inputScopesError: String = definedExternally
        var endpointResolutionError: String = definedExternally
        var popUpWindowError: String = definedExternally
        var userLoginError: String = definedExternally
    }
}
external open class ErrorDescription {
    companion object {
        var loginProgressError: String = definedExternally
        var acquireTokenProgressError: String = definedExternally
        var inputScopesError: String = definedExternally
        var endpointResolutionError: String = definedExternally
        var popUpWindowError: String = definedExternally
        var userLoginError: String = definedExternally
    }
}
external open class ErrorMessage {
    companion object {
        var authorityUriInvalidPath: String = definedExternally
        var authorityUriInsecure: String = definedExternally
        var invalidAuthorityType: String = definedExternally
        var unsupportedAuthorityValidation: String = definedExternally
        var b2cAuthorityUriInvalidPath: String = definedExternally
    }
}
external open class IdToken(rawIdToken: String) {
    open var rawIdToken: String = definedExternally
    open var issuer: String = definedExternally
    open var objectId: String = definedExternally
    open var subject: String = definedExternally
    open var tenantId: String = definedExternally
    open var version: String = definedExternally
    open var preferredName: String = definedExternally
    open var name: String = definedExternally
    open var homeObjectId: String = definedExternally
    open var nonce: String = definedExternally
    open var expiration: String = definedExternally
}
external interface IInstanceDiscoveryResponse {
    var TenantDiscoveryEndpoint: String
}
external interface ITenantDiscoveryResponse {
    var AuthorizationEndpoint: String
    var EndSessionEndpoint: String
    var Issuer: String
}
external interface IUri {
    var Protocol: String
    var HostNameAndPort: String
    var AbsolutePath: String
    var Search: String
    var Hash: String
    var PathSegments: Array<String>
}
external enum class LogLevel {
    Error /* = 0 */,
    Warning /* = 1 */,
    Info /* = 2 */,
    Verbose /* = 3 */
}
external open class Logger(correlationId: String) {
    open var _correlationId: Any = definedExternally
    open var correlationId: String = definedExternally
    open var _level: Any = definedExternally
    open var level: LogLevel = definedExternally
    open var _piiLoggingEnabled: Any = definedExternally
    open var piiLoggingEnabled: Boolean = definedExternally
    open var _localCallback: Any = definedExternally
    open var localCallback: ILoggerCallback = definedExternally
    open fun logMessage(logMessage: Any, logLevel: Any, containsPii: Any): Unit = definedExternally
    open fun executeCallback(level: LogLevel, message: String, containsPii: Boolean): Unit = definedExternally
    open fun error(message: String): Unit = definedExternally
    open fun errorPii(message: String): Unit = definedExternally
    open fun warning(message: String): Unit = definedExternally
    open fun warningPii(message: String): Unit = definedExternally
    open fun info(message: String): Unit = definedExternally
    open fun infoPii(message: String): Unit = definedExternally
    open fun verbose(message: String): Unit = definedExternally
    open fun verbosePii(message: String): Unit = definedExternally
    companion object {
        var _instance: Any = definedExternally
    }
}
external open class RequestContext(correlationId: String) {
    open var _correlationId: Any = definedExternally
    open var correlationId: String = definedExternally
    open var _logger: Any = definedExternally
    open var logger: Logger = definedExternally
    companion object {
        var _instance: Any = definedExternally
    }
}
external open class TokenResponse {
    open var valid: Boolean = definedExternally
    open var parameters: Any = definedExternally
    open var stateMatch: Boolean = definedExternally
    open var stateResponse: String = definedExternally
    open var requestType: String = definedExternally
}
external open class Storage(cacheLocation: String) {
    open var _localStorageSupported: Any = definedExternally
    open var _sessionStorageSupported: Any = definedExternally
    open var _cacheLocation: Any = definedExternally
    open fun setItem(key: String, value: String): Unit = definedExternally
    open fun getItem(key: String): String = definedExternally
    open fun removeItem(key: String): Unit = definedExternally
    open fun clear(): Unit = definedExternally
    open fun getAllAccessTokens(clientId: String, userIdentifier: String): Array<AccessTokenCacheItem> = definedExternally
    open fun removeAcquireTokenEntries(acquireTokenUser: String, acquireTokenStatus: String): Unit = definedExternally
    open fun resetCacheItems(): Unit = definedExternally
    companion object {
        var _instance: Any = definedExternally
    }
}
external open class Telemetry {
    open var receiverCallback: Any = definedExternally
    open fun RegisterReceiver(receiverCallback: (receiver: Array<Any>) -> Unit): Unit = definedExternally
    companion object {
        var instance: Any = definedExternally
        fun GetInstance(): Telemetry = definedExternally
    }
}
external open class User(displayableId: String, name: String, identityProvider: String, userIdentifier: String) {
    open var displayableId: String = definedExternally
    open var name: String = definedExternally
    open var identityProvider: String = definedExternally
    open var userIdentifier: String = definedExternally
    companion object {
        fun createUser(idToken: IdToken, clientInfo: ClientInfo, authority: String): User = definedExternally
    }
}
external open class UserAgentApplication(
        clientId: String,
        authority: String? = definedExternally /* null */,
        tokenReceivedCallback: ((errorDesc: String, token: String, error: String, tokenType: String) -> Unit)? = definedExternally /* null */,
        validateAuthority: Boolean? = definedExternally /* null */
) {
    open var _cacheLocations: Any = definedExternally
    open var _cacheLocation: Any = definedExternally
    open var cacheLocation: String = definedExternally
    open var _interactionModes: Any = definedExternally
    open var _interactionMode: Any = definedExternally
    open var _requestContext: Any = definedExternally
    open var _loginInProgress: Any = definedExternally
    open var _acquireTokenInProgress: Any = definedExternally
    open var _renewStates: Any = definedExternally
    open var _activeRenewals: Any = definedExternally
    open var _clockSkew: Any = definedExternally
    open var _cacheStorage: Any = definedExternally
    open var _tokenReceivedCallback: Any = definedExternally
    open var _user: Any = definedExternally
    open var clientId: String = definedExternally
    open var authorityInstance: Any = definedExternally
    open var authority: String = definedExternally
    open var validateAuthority: Boolean = definedExternally
    open var redirectUri: String = definedExternally
    open var postLogoutredirectUri: String = definedExternally
    open var navigateToLoginRequestUrl: Boolean = definedExternally
    open fun loginRedirect(scopes: Array<String>? = definedExternally /* null */, extraQueryParameters: String? = definedExternally /* null */): Unit = definedExternally
    open fun loginPopup(
            scopes: Array<String>? = definedExternally /* null */,
            extraQueryParameters: String? = definedExternally /* null */
    ): Promise<String> = definedExternally
    open fun promptUser(urlNavigate: Any): Unit = definedExternally
    open fun openWindow(urlNavigate: Any, title: Any, interval: Any, instance: Any, resolve: Any? = definedExternally /* null */, reject: Any? = definedExternally /* null */): Unit = definedExternally
    open fun logout(): Unit = definedExternally
    open fun clearCache(): Unit = definedExternally
    open fun openPopup(urlNavigate: Any, title: Any, popUpWidth: Any, popUpHeight: Any): Unit = definedExternally
    open fun validateInputScope(scopes: Any): Unit = definedExternally
    open fun filterScopes(scopes: Any): Unit = definedExternally
    open fun registerCallback(expectedState: Any, scope: Any, resolve: Any, reject: Any): Unit = definedExternally
    open fun getCachedToken(authenticationRequest: Any, user: Any): Unit = definedExternally
    open fun getAllUsers(): Array<User> = definedExternally
    open fun getUniqueUsers(users: Any): Unit = definedExternally
    open fun getUniqueAuthority(accessTokenCacheItems: Any, property: Any): Unit = definedExternally
    open fun addHintParameters(urlNavigate: Any, user: Any): Unit = definedExternally
    open fun urlContainsQueryStringParameter(name: Any, url: Any): Unit = definedExternally
    open fun acquireTokenRedirect(scopes: Array<String>): Unit = definedExternally
    open fun acquireTokenRedirect(scopes: Array<String>, authority: String): Unit = definedExternally
    open fun acquireTokenRedirect(scopes: Array<String>, authority: String, user: User): Unit = definedExternally
    open fun acquireTokenRedirect(scopes: Array<String>, authority: String, user: User, extraQueryParameters: String): Unit = definedExternally
    open fun acquireTokenPopup(scopes: Array<String>): Promise<String> = definedExternally
    open fun acquireTokenPopup(scopes: Array<String>, authority: String): Promise<String> = definedExternally
    open fun acquireTokenPopup(scopes: Array<String>, authority: String, user: User): Promise<String> = definedExternally
    open fun acquireTokenPopup(scopes: Array<String>, authority: String, user: User, extraQueryParameters: String): Promise<String> = definedExternally
    open fun acquireTokenSilent(scopes: Array<String>, authority: String? = definedExternally /* null */, user: User? = definedExternally /* null */, extraQueryParameters: String? = definedExternally /* null */): Promise<String> = definedExternally
    open fun loadFrameTimeout(urlNavigate: Any, frameName: Any, scope: Any): Unit = definedExternally
    open fun loadFrame(urlNavigate: Any, frameName: Any): Unit = definedExternally
    open fun addAdalFrame(iframeId: Any): Unit = definedExternally
    open fun renewToken(scopes: Any, resolve: Any, reject: Any, user: Any, authenticationRequest: Any, extraQueryParameters: Any? = definedExternally /* null */): Unit = definedExternally
    open fun renewIdToken(scopes: Any, resolve: Any, reject: Any, user: Any, authenticationRequest: Any, extraQueryParameters: Any? = definedExternally /* null */): Unit = definedExternally
    open fun getUser(): User? = definedExternally
    open fun handleAuthenticationResponse(hash: String, resolve: Function<*>? = definedExternally /* null */, reject: Function<*>? = definedExternally /* null */): Unit = definedExternally
    open fun saveAccessToken(authority: Any, tokenResponse: Any, user: Any, clientInfo: Any, idToken: Any): Unit = definedExternally
    open fun saveTokenFromHash(tokenResponse: Any): Unit = definedExternally
    open fun isCallback(hash: String): Boolean = definedExternally
    open fun getHash(hash: Any): Unit = definedExternally
    open fun getRequestInfo(hash: Any): Unit = definedExternally
    open fun getScopeFromState(state: Any): Unit = definedExternally
}
external open class Utils {
    companion object {
        fun compareObjects(u1: User, u2: User): Boolean = definedExternally
        fun expiresIn(expires: String): Number = definedExternally
        fun now(): Number = definedExternally
        fun isEmpty(str: String): Boolean = definedExternally
        fun extractIdToken(encodedIdToken: String): Any = definedExternally
        fun base64EncodeStringUrlSafe(input: String): String = definedExternally
        fun base64DecodeStringUrlSafe(base64IdToken: String): String = definedExternally
        fun encode(input: String): String = definedExternally
        fun utf8Encode(input: String): String = definedExternally
        fun decode(base64IdToken: String): String = definedExternally
        fun decodeJwt(jwtToken: String): Any = definedExternally
        fun deserialize(query: String): Any = definedExternally
        fun isIntersectingScopes(cachedScopes: Array<String>, scopes: Array<String>): Boolean = definedExternally
        fun containsScope(cachedScopes: Array<String>, scopes: Array<String>): Boolean = definedExternally
        fun convertToLowerCase(scopes: Array<String>): Array<String> = definedExternally
        fun removeElement(scopes: Array<String>, scope: String): Array<String> = definedExternally
        fun decimalToHex(num: Number): String = definedExternally
        fun getLibraryVersion(): String = definedExternally
        fun replaceFirstPath(href: String, tenantId: String): String = definedExternally
        fun createNewGuid(): String = definedExternally
        fun GetUrlComponents(url: String): IUri = definedExternally
        fun CanonicalizeUri(url: String): String = definedExternally
        fun endsWith(url: String, suffix: String): Boolean = definedExternally
    }
}
external open class XhrClient {
    open fun sendRequestAsync(url: String, method: String, enableCaching: Boolean? = definedExternally /* null */): Promise<Any> = definedExternally
    open fun handleError(responseText: String): Any = definedExternally
}
