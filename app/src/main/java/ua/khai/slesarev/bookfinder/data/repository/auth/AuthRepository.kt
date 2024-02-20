package ua.khai.slesarev.bookfinder.data.repository.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.ui.util.UiState

interface AuthRepository {

    suspend fun signUpWithEmailPassword(email:String, password:String, username: String): Response<Event>
    suspend fun signInWithEmailPassword(email:String, password:String): Response<Event>
    fun signOut(): Response<Event>
    fun signOutGoogle(): Response<Event>
    suspend fun resetPassword(email: String): Response<Event>
    suspend fun signInWithGoogle(account: GoogleSignInAccount, token:String): Response<Event>
    fun getGoogleSignInIntent(context: Context): Intent
}