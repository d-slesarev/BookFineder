package ua.khai.slesarev.bookfinder.data.remote.api.authentication.googlesingin

import android.content.Context
import android.content.Intent
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response

interface FirebaseAuthService {

    suspend fun signUpWithEmailPassword(email:String, password:String, username: String): Event
    suspend fun signInWithEmailPassword(email:String, password:String): Event
    fun signOut(): Event
    fun signOutGoogle(): Event
    suspend fun signInWithGoogle(token:String): Response<Event>
    suspend fun sendEmailVerification(): Event
    fun getGoogleSignInIntent(context: Context): Intent
    suspend fun resetPassword(email: String): Event
    suspend fun rollBackRegister(): Event

}