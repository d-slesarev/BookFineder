package ua.khai.slesarev.bookfinder.data.remote.api.authentication.appoauth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import org.json.JSONException
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthStateManager private constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: AuthStateManager? = null

        fun init(context: Context) {
            Log.i(MY_TAG, "AuthStateManager.init(): Activated!")
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = AuthStateManager(context)
                    }
                }
            }
        }

        fun getInstance(): AuthStateManager {
            Log.i(MY_TAG, "AuthStateManager.getInstance(): Activated!")
            return INSTANCE ?: throw IllegalStateException("AuthStateManager is not initialized, call init(context) first")
        }

        private const val SHARED_PREFERENCES_NAME = "AuthStatePreferences"
        private const val AUTH_STATE_KEY = "AUTH_STATE"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    var authState: AuthState
        get() = readAuthState()
        set(value) = preferences.edit().putString(AUTH_STATE_KEY, value.jsonSerializeString()).apply()

    val authService: AuthorizationService

    init {
        authState = readAuthState()
        authService = AuthorizationService(context)
    }

    private fun readAuthState(): AuthState {
        val jsonString = preferences.getString(AUTH_STATE_KEY, null)
        return if (jsonString != null) {
            try {
                Log.d(MY_TAG, "AuthStateManager.readAuthState(): SUCCESS!")
                AuthState.jsonDeserialize(jsonString)
            } catch (e: JSONException) {
                AuthState()
            }
        } else {
            AuthState()
        }
    }

    suspend fun getAccessToken(): String? {
        return if (authState.isAuthorized) {
            if (authState.needsTokenRefresh) {
                Log.d(MY_TAG, "AuthStateManager.refreshAccessToken(): Started!")
                refreshAccessToken()
            } else {
                authState.accessToken
            }
        } else {
            null
        }
    }

     suspend fun refreshAccessToken(): String? {
        return suspendCancellableCoroutine { continuation ->
            authState.performActionWithFreshTokens(authService) { accessToken, idToken, ex ->
                if (ex == null) {
                    accessToken?.let {
                        continuation.resume(it)
                    } ?: continuation.resume(null)
                    Log.d(MY_TAG, "AuthStateManager.refreshAccessToken(): SUCCESS!\nNewAccessToken: $accessToken")
                } else {
                    Log.d(MY_TAG, "AuthStateManager.refreshAccessToken(): FAILURE!\nMessage: ${ex.message}")
                    continuation.resumeWithException(ex)
                }
            }
        }
    }

    fun getIdToken(): String? {
        if (authState.isAuthorized) {
            return authState.idToken
        }
        return null
    }

    fun clearAuthState() {
        preferences.edit().remove(AUTH_STATE_KEY).apply()
    }

    fun isLoggedIn(): Boolean {
        return authState.isAuthorized
    }
}