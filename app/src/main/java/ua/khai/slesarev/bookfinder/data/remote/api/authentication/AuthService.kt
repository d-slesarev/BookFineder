package ua.khai.slesarev.bookfinder.data.remote.api.authentication

import ua.khai.slesarev.bookfinder.data.util.Event

interface AuthService {

    suspend fun signUpWithEmailPassword(email:String, password:String, username: String): Event
    suspend fun signInWithEmailPassword(email:String, password:String): Event
    fun signOut(): Event
    suspend fun signInWithGoogle(): Event
    suspend fun sendEmailVerification(): Event
    suspend fun resetPassword(email: String): Event
    suspend fun rollBackRegister(): Event

}