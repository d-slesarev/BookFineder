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
import net.openid.appauth.AuthorizationResponse
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.TokenStorage
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl


class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val authHelper: AuthRepository = AuthRepositoryImpl(application)
    private val userRepo: UserRepository = UserRepositoryImpl(application)
    private val authRepository = OAuthRepository()
    private val authService: AuthorizationService = AuthorizationService(application)

    val uiState: MutableLiveData<UiState> = MutableLiveData()
    private val loadingMutableStateFlow = MutableStateFlow(true)

    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val tokenReceiptSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)
    private val firebaseAuthSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)
    private val loadProfileSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()
    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()
    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()
    val tokenReceiptSuccessFlow: Flow<Unit>
        get() = tokenReceiptSuccessEventChannel.receiveAsFlow()
    val firebaseAuthSuccessFlow: Flow<Unit>
        get() = firebaseAuthSuccessEventChannel.receiveAsFlow()

    val loadProfileSuccessFlow: Flow<Unit>
        get() = loadProfileSuccessEventChannel.receiveAsFlow()

    fun signInWithGoogle() {
        loadingMutableStateFlow.value = false
        viewModelScope.launch {
            runCatching {
                TokenStorage.idToken?.let {
                    authHelper.signInWithGoogle(it)
                }
            }.onSuccess {
                firebaseAuthSuccessEventChannel.send(Unit)
            }.onFailure {
                loadingMutableStateFlow.value = true
                toastEventChannel.send(R.string.auth_canceled)
            }
        }
    }

    fun loadUserProfile(){
        loadingMutableStateFlow.value = false
        viewModelScope.launch {
            runCatching {
                TokenStorage.accessToken?.let {
                    userRepo.loadUserFromAPI(it)
                }
            }.onSuccess {
                loadProfileSuccessEventChannel.send(Unit)
            }.onFailure {
                toastEventChannel.send(R.string.auth_canceled)
            }
        }
    }

    fun onAuthCodeFailed(exception: AuthorizationException) {

    }

    fun onAuthCodeReceived(tokenRequest: TokenRequest, authResponse: AuthorizationResponse) {

        Log.d(MY_TAG, "3. Received code = ${tokenRequest.authorizationCode}")
        loadingMutableStateFlow.value = false

        viewModelScope.launch {
            runCatching {
                Log.d(MY_TAG, "4. Change code to token. Url = ${tokenRequest.configuration.tokenEndpoint}, verifier = ${tokenRequest.codeVerifier}")
                authRepository.performTokenRequest(
                    authService = authService,
                    tokenRequest = tokenRequest,
                    authResponse = authResponse
                )
            }.onSuccess {
                tokenReceiptSuccessEventChannel.send(Unit)
            }.onFailure {
                loadingMutableStateFlow.value = true
                toastEventChannel.send(R.string.auth_canceled)
            }
        }
    }


    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val authRequest = authRepository.getAuthRequest()

        Log.d(MY_TAG, "1. Generated verifier=${authRequest.codeVerifier},challenge=${authRequest.codeVerifierChallenge}")

        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRequest,
            customTabsIntent
        )

        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)

        Log.d(MY_TAG, "2. Open auth page: ${authRequest.toUri()}")
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }

}