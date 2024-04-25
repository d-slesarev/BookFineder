package ua.khai.slesarev.bookfinder.data.repository.authentication

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response

interface AuthRepository {

    fun signOut(): Response<Event>
    fun signOutGoogle(): Response<Event>
    suspend fun resetPassword(email: String): Response<Event>
    suspend fun signInWithGoogle(token:String)
    fun getGoogleSignInIntent(context: Context): Intent
}