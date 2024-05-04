package ua.khai.slesarev.bookfinder.data.remote.api.authentication.appoauth

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretPost
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import ua.khai.slesarev.bookfinder.data.model.TokensModel
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.TokenStorage
import ua.khai.slesarev.bookfinder.data.util.AuthConfig
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import kotlin.coroutines.suspendCoroutine

object OAuthManager {
    private val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.URL_AUTHORIZATION),
        Uri.parse(AuthConfig.URL_TOKEN_EXCHANGE),
        null, // registration endpoint
        Uri.parse(AuthConfig.URL_LOGOUT)
    )

    fun getAuthRequest(): AuthorizationRequest {
        val redirectUri = AuthConfig.URL_AUTH_REDIRECT.toUri()

        val authState = AuthState()

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScopes(AuthConfig.SCOPE_PROFILE, AuthConfig.SCOPE_BOOKS, AuthConfig.SCOPE_EMAIL, AuthConfig.SCOPE_OPENID)
            .build()
    }

    fun getEndSessionRequest(): EndSessionRequest {
        val configuration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.URL_AUTHORIZATION),
            Uri.parse(AuthConfig.URL_TOKEN_EXCHANGE),
            null, // registration endpoint
            Uri.parse(AuthConfig.URL_LOGOUT + "${TokenStorage.accessToken}")
        )

        return EndSessionRequest.Builder(configuration)
            .setPostLogoutRedirectUri(AuthConfig.URL_LOGOUT_REDIRECT.toUri())
            .build()
    }

    fun getRefreshTokenRequest(refreshToken: String): TokenRequest {
        return TokenRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setScope(AuthConfig.SCOPE_BOOKS)
            .setScope(AuthConfig.SCOPE_PROFILE)
            .setRefreshToken(refreshToken)
            .build()
    }

    suspend fun performTokenRequestSuspend(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): TokensModel {
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(tokenRequest) { response, ex ->
                when {
                    response != null -> {
                        //получение токена произошло успешно
                        val tokens = TokensModel(
                            accessToken = response.accessToken.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty(),
                            idToken = response.idToken.orEmpty()
                        )
                        Log.d(MY_TAG, "performTokenRequestSuspend.idToken: ${response.idToken.orEmpty()}")
                        continuation.resumeWith(Result.success(tokens))
                    }
                    //получение токенов произошло неуспешно, показываем ошибку
                    ex != null -> { continuation.resumeWith(Result.failure(ex)) }
                    else -> error("unreachable")
                }
            }
        }
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }
}