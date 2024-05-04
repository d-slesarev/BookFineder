package ua.khai.slesarev.bookfinder.ui.home_screen

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationService
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.repository.authentication.AuthRepository
import ua.khai.slesarev.bookfinder.data.repository.authentication.AuthRepositoryImpl
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.OAuthRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var auth: FirebaseAuth = Firebase.auth
    private val authService: AuthorizationService = AuthorizationService(getApplication())
    private val authRepository = OAuthRepository()
    private val userRepo: UserRepository = UserRepositoryImpl(application)
    private var authRepo: AuthRepository = AuthRepositoryImpl(application)
    private var defUser: List<User> = listOf(User(username = "Unknown Unknownson", email = "unknownson@unknown.com", imageUri = ""))

    private val getCurrentUserEventChannel = Channel<List<User>>(Channel.BUFFERED)
    private val logoutPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val logoutCompletedEventChannel = Channel<Unit>(Channel.BUFFERED)
    val getCurrentUserSuccessFlow: Flow<List<User>>
        get() =  getCurrentUserEventChannel.receiveAsFlow()
    val logoutPageFlow: Flow<Intent>
        get() = logoutPageEventChannel.receiveAsFlow()

    val logoutCompletedFlow: Flow<Unit>
        get() = logoutCompletedEventChannel.receiveAsFlow()

    fun signOut() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val endSessionRequest = authRepository.getEndSessionRequest()

        val logoutPageIntent = authService.getEndSessionRequestIntent(
            endSessionRequest,
            customTabsIntent
        )

        logoutPageEventChannel.trySendBlocking(logoutPageIntent)

        Log.i(MY_TAG, "###. Open end session page: ${endSessionRequest.toUri()}")
    }

    fun webLogoutComplete() {
        authRepository.logout()
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userRepo.deleteAllUsers()
            }.onSuccess {
                logoutCompletedEventChannel.trySendBlocking(Unit)
            }.onFailure {
                logoutCompletedEventChannel.trySendBlocking(Unit)
                Log.d(MY_TAG, "HomeActViewModel.getCurrentUser-Exception: ${it.message}")
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userRepo.getAllUsers()
            }.onSuccess {
                getCurrentUserEventChannel.trySendBlocking(it.getOrDefault(defUser))
            }.onFailure {
                getCurrentUserEventChannel.trySendBlocking(defUser)
                Log.d(MY_TAG, "HomeActViewModel.getCurrentUser-Exception: ${it.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}