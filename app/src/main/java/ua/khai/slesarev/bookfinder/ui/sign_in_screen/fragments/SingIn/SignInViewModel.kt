package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import net.openid.appauth.AuthorizationService
import ua.khai.slesarev.bookfinder.data.repository.authentication.AuthRepository
import ua.khai.slesarev.bookfinder.data.repository.authentication.AuthRepositoryImpl
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.OAuthRepository
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.ui.util.UiState
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.TokenRequest
import kotlinx.coroutines.flow.receiveAsFlow
import ua.khai.slesarev.bookfinder.R


class SignInViewModel(private val application: Application) : AndroidViewModel(application) {

    private val authHelper: AuthRepository = AuthRepositoryImpl(application)
    private val authRepository = OAuthRepository()
    private val authService: AuthorizationService = AuthorizationService(getApplication())

    val uiState: MutableLiveData<UiState> = MutableLiveData()
    private val loadingMutableStateFlow = MutableStateFlow(true)

    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()
    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()
    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()
    val authSuccessFlow: Flow<Unit>
        get() = authSuccessEventChannel.receiveAsFlow()

    suspend fun signInWithEmailPassword(email: String, password: String) {
        uiState.value = UiState.Loading

        val result = authHelper.signInWithEmailPassword(email, password)

        if (result is Response.Success) {
            uiState.value = UiState.Success(result.data.toString())
        } else if (result is Response.Error) {
            authHelper.signOut()
            uiState.value = UiState.Error(result.errorMessage)
        }
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount, token: String) {
        uiState.value = UiState.Loading
        val result: Response<Event> = authHelper.signInWithGoogle(account, token)

        if (result is Response.Success) {
            Log.d(MY_TAG, "result: ${result.data}")
            uiState.value = UiState.Success(result.data.toString())
        } else if (result is Response.Error) {
            uiState.value = UiState.Error(result.errorMessage)
        }
    }
    fun getGoogleSignInIntent(context: Context): Intent {
      return authHelper.getGoogleSignInIntent(context)
    }

    fun onAuthCodeFailed(exception: AuthorizationException) {

    }

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {

        Log.d("Oauth", "3. Received code = ${tokenRequest.authorizationCode}")
        loadingMutableStateFlow.value = true

        viewModelScope.launch {
            runCatching {
                Log.d("Oauth", "4. Change code to token. Url = ${tokenRequest.configuration.tokenEndpoint}, verifier = ${tokenRequest.codeVerifier}")
                authRepository.performTokenRequest(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                loadingMutableStateFlow.value = false
                authSuccessEventChannel.send(Unit)
            }.onFailure {
                loadingMutableStateFlow.value = false
                toastEventChannel.send(R.string.auth_canceled)
            }
        }
    }


    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val authRequest = authRepository.getAuthRequest()

        Log.d("Oauth", "1. Generated verifier=${authRequest.codeVerifier},challenge=${authRequest.codeVerifierChallenge}")

        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRequest,
            customTabsIntent
        )

        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)

        Log.d("Oauth", "2. Open auth page: ${authRequest.toUri()}")
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }

}