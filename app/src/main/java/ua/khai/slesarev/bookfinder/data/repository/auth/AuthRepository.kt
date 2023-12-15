package ua.khai.slesarev.bookfinder.data.repository.auth

import android.app.Activity
import android.content.Intent
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.ui.util.UiState

interface AuthRepository {

    suspend fun signUpWithEmailPassword(email:String, password:String, username: String): Response<Event>
    suspend fun signInWithEmailPassword(email:String, password:String): Response<Event>
    fun signOut(): Response<Event>
    suspend fun resetPassword(email: String): Response<Event>
    suspend fun signInWithGoogle(): Response<Event>
    fun getGoogleSignInIntent(activity: Activity): Intent
}