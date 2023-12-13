package ua.khai.slesarev.bookfinder.data.remote.api.authentication.impl

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.AuthService
import ua.khai.slesarev.bookfinder.data.util.Event
import kotlin.coroutines.resume

class FirebaseAuthService : AuthService {

    private val TAG = "FirebaseAuth"
    private var auth: FirebaseAuth = Firebase.auth

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        username: String
    ): Event {
        return suspendCancellableCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail: SUCCESS!")
                        continuation.resume(Event.SUCCESS)
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthException) {
                            val errorCode = exception.errorCode
                            when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    continuation.resume(Event.ERROR_INVALID_EMAIL)
                                }

                                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                    continuation.resume(Event.ERROR_EMAIL_ALREADY_IN_USE)
                                }

                                "ERROR_INVALID_CREDENTIAL" -> {
                                    continuation.resume(Event.ERROR_INVALID_CREDENTIAL)
                                }

                                "ERROR_WEAK_PASSWORD" -> {
                                    continuation.resume(Event.ERROR_WEAK_PASSWORD)
                                }

                                else -> {
                                    continuation.resume(Event.ERROR_UNKNOWN)
                                }
                            }
                        } else {
                            if (exception != null) {
                                Log.d(TAG, "sendEmailVerification: " + exception.message)
                            }
                            continuation.resume(Event.ERROR_UNKNOWN)
                        }
                    }
                }
        }
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): Event {
        return suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Sing In: SUCCESS!")

                        val user = auth.currentUser

                        if (user != null && user.isEmailVerified){
                            continuation.resume(Event.SUCCESS)
                        } else {
                            continuation.resume(Event.ERROR_UNCONFIRMED_EMAIL)
                            auth.signOut()
                        }

                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthException) {
                            val errorCode = exception.errorCode
                            when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    continuation.resume(Event.ERROR_INVALID_EMAIL)
                                }

                                "ERROR_USER_NOT_FOUND" -> {
                                    continuation.resume(Event.ERROR_USER_NOT_FOUND)
                                }

                                "ERROR_INVALID_CREDENTIAL" -> {
                                    continuation.resume(Event.ERROR_INVALID_CREDENTIAL)
                                }

                                else -> {
                                    continuation.resume(Event.ERROR_UNKNOWN)
                                }
                            }
                        } else {
                            if (exception != null) {
                                Log.d(TAG, "Sing In: " + exception.message)
                            }
                            continuation.resume(Event.ERROR_UNKNOWN)
                        }
                    }
                }
        }
    }

    override fun signOut(): Event {
        auth.signOut()
        return Event.SUCCESS
    }

    override suspend fun signInWithGoogle(): Event {
        TODO("Not yet implemented")
    }

    override suspend fun sendEmailVerification(): Event {
        return suspendCancellableCoroutine { continuation ->
            Log.w(TAG, "sendEmailVerification: Started!")
            val user = auth.currentUser

            if (user != null) {
                try {
                    user.sendEmailVerification()
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            auth.signOut()
                            Log.d(TAG, "sendEmailVerification: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(TAG, "sendEmailVerification: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }
            Log.w(TAG, "sendEmailVerification: Finished!")
        }
    }

    override suspend fun resetPassword(email: String): Event {
        TODO("Not yet implemented")
    }

    override suspend fun rollBackRegister(): Event {
        return suspendCancellableCoroutine { continuation ->
            val user = auth.currentUser
            if (user != null) {
                try {
                    user.delete()
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            Log.d(TAG, "rollBackRegister: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(TAG, "rollBackRegister: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }
        }
    }

}