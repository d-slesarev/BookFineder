package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepository
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepositoryImpl
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.ui.util.UiState
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import kotlin.coroutines.resume

class SignInViewModel(private val application: Application) : AndroidViewModel(application) {

    private val authHelper: AuthRepository = AuthRepositoryImpl(application)

    val uiState: MutableLiveData<UiState> = MutableLiveData()

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

}