package ua.khai.slesarev.bookfinder.data.repository.authentication

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response

interface AuthRepository {

    suspend fun signUpWithEmailPassword(email:String, password:String, username: String): Response<Event>
    suspend fun signInWithEmailPassword(email:String, password:String): Response<Event>
    fun signOut(): Response<Event>
    fun signOutGoogle(): Response<Event>
    suspend fun resetPassword(email: String): Response<Event>
    suspend fun signInWithGoogle(account: GoogleSignInAccount, token:String): Response<Event>
    fun getGoogleSignInIntent(context: Context): Intent
}