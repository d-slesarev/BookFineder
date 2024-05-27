package ua.khai.slesarev.bookfinder.ui.home_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import ua.khai.slesarev.bookfinder.data.models.local.User
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.appoauth.AuthStateManager
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.OAuthRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class HomeActivityViewModel(private val application: Application) : AndroidViewModel(application) {
    private val authService: AuthorizationService = AuthorizationService(application)
    private val authRepo = OAuthRepository()
    private val authState: AuthStateManager = AuthStateManager.getInstance()
    private val userRepo: UserRepository = UserRepositoryImpl(application)
    private var defUser: List<User> = listOf(User(username = "Unknown Unknownson", email = "unknownson@unknown.com", imageUri = ""))

    private val getCurrentUserEventChannel = Channel<List<User>>(Channel.BUFFERED)
    private val revokeTokenCompletedEventChannel = Channel<Unit>(Channel.BUFFERED)
    private val logoutCompletedEventChannel = Channel<Unit>(Channel.BUFFERED)
    val getCurrentUserSuccessFlow: Flow<List<User>>
        get() =  getCurrentUserEventChannel.receiveAsFlow()

    val revokeTokenCompletedFlow: Flow<Unit>
        get() = revokeTokenCompletedEventChannel.receiveAsFlow()

    val logoutCompletedFlow: Flow<Unit>
        get() = logoutCompletedEventChannel.receiveAsFlow()

    fun signOut() {
        Log.i(MY_TAG, "signOut(): Started!")
        viewModelScope.launch{
            runCatching {
                authRepo.performEndSessionRequest()
            }.onSuccess {
                revokeTokenCompletedEventChannel.trySendBlocking(Unit)
            }.onFailure {
                revokeTokenCompletedEventChannel.trySendBlocking(Unit)
            }
        }
    }

    fun webLogoutComplete() {
        authState.clearAuthState()
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userRepo.deleteAllUsers()
            }.onSuccess {
                logoutCompletedEventChannel.trySendBlocking(Unit)
            }.onFailure {
                logoutCompletedEventChannel.trySendBlocking(Unit)
            }
        }
    }

    fun getCurrentUser() {
        Log.d(MY_TAG, "HomeActivity.getCurrentUser(): Started!")
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