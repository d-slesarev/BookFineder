package ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth

import android.app.PendingIntent
import android.util.Log
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.appoauth.OAuthManager
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class OAuthRepository {
    fun logout() {
        TokenStorage.accessToken = null
        TokenStorage.refreshToken = null
        TokenStorage.idToken = null
    }

    fun getAuthRequest(): AuthorizationRequest {
        return OAuthManager.getAuthRequest()
    }

    suspend fun performEndSessionRequest(){
        TokenStorage.accessToken?.let {
            OAuthManager.performEndSessionRequest(it)
        }
    }

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        authResponse: AuthorizationResponse
    ) {
        val tokens = OAuthManager.performTokenRequestSuspend(authService, tokenRequest, authResponse)
        //обмен кода на токен произошел успешно, сохраняем токены и завершаем авторизацию
        TokenStorage.accessToken = tokens.accessToken
        TokenStorage.refreshToken = tokens.refreshToken
        TokenStorage.idToken = tokens.idToken
        Log.d(MY_TAG, "6. Tokens accepted:\n access=${tokens.accessToken}\nrefresh=${tokens.refreshToken}\nidToken=${tokens.idToken}")
    }
}