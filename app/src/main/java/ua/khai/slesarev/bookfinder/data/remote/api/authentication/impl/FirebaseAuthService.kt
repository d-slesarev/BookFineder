package ua.khai.slesarev.bookfinder.data.remote.api.authentication.impl

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.AuthService
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.data.util.Response
import kotlin.coroutines.resume

class FirebaseAuthService(private val context: Context) : AuthService {

    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var signInClient: GoogleSignInClient

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        username: String
    ): Event {
        return suspendCancellableCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(MY_TAG, "FirebaseAuthServ.signUp: SUCCESS!")
                        continuation.resume(Event.SUCCESS)
                    } else {
                        Log.d(MY_TAG, "FirebaseAuthServ.signUp: FAILURE!")
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
                                Log.d(MY_TAG, "FirebaseAuthServ.signUp-Exception: " + exception.message)
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
                        Log.d(MY_TAG, "FirebaseAuthServ.SingIn(): SUCCESS!")

                        val user = auth.currentUser

                        if (user != null && user.isEmailVerified){
                            continuation.resume(Event.SUCCESS)
                        } else {
                            continuation.resume(Event.ERROR_UNCONFIRMED_EMAIL)
                            auth.signOut()
                        }

                    } else {
                        Log.d(MY_TAG, "FirebaseAuthServ.SingIn(): FAILURE!")
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

                                "ERROR_WRONG_PASSWORD" -> {
                                    continuation.resume(Event.ERROR_WRONG_PASSWORD)
                                }

                                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                    continuation.resume(Event.ERROR_EMAIL_ALREADY_IN_USE)
                                }

                                else -> {
                                    continuation.resume(Event.ERROR_UNKNOWN)
                                }
                            }
                        } else {
                            if (exception != null) {
                                Log.d(MY_TAG, "FirebaseAuthServ.SingIn()-Exception: " + exception.message)
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

    override fun signOutGoogle(): Event {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile()
            .requestIdToken(context.getString(R.string.default_web_client_id)).build()
        signInClient = GoogleSignIn.getClient(context, gso)

        signInClient.signOut()

        return Event.SUCCESS
    }

    override suspend fun resetPassword(email: String): Event {
        return suspendCancellableCoroutine { continuation ->
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(MY_TAG, "FirebaseAuthServ.resetPass: SUCCESS!")
                        continuation.resume(Event.SUCCESS)
                    } else {
                        Log.d(MY_TAG, "FirebaseAuthServ.resetPass: FAILURE!")
                        val exception = task.exception
                        if (exception is FirebaseAuthException) {
                            when (exception.errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    continuation.resume(Event.ERROR_INVALID_EMAIL)
                                }

                                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                    continuation.resume(Event.ERROR_EMAIL_ALREADY_IN_USE)
                                }

                                "ERROR_USER_NOT_FOUND" -> {
                                    continuation.resume(Event.ERROR_USER_NOT_FOUND)
                                }

                                "ERROR_TOO_MANY_REQUESTS" -> {
                                    continuation.resume(Event.ERROR_TOO_MANY_REQUESTS)
                                }

                                else -> {
                                    continuation.resume(Event.ERROR_UNKNOWN)
                                }
                            }
                        } else {
                            if (exception != null) {
                                Log.d(MY_TAG, "FirebaseAuthServ.resetPass-Exception: " + exception.message)
                            }
                            continuation.resume(Event.ERROR_UNKNOWN)
                        }
                    }
                }
        }
    }

    override suspend fun signInWithGoogle(token: String): Response<Event> {
        return suspendCancellableCoroutine { continuation ->
            val credential = GoogleAuthProvider.getCredential(token, null)
            Log.d(MY_TAG, "credential: $credential")
            Log.i(MY_TAG, "Before: FirebaseAuthServ.signInWithGoogle()")
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Log.d(MY_TAG, "FirebaseAuthServ.signInWithGoogle(): SUCCESS!")
                        val isNewUser = task.result?.additionalUserInfo?.isNewUser
                        if (isNewUser == true) {
                            continuation.resume(Response.Success(Event.NEW_USER))
                        } else {
                            continuation.resume(Response.Success(Event.OLD_USER))
                        }
                    } else {
                        Log.d(MY_TAG, "FirebaseAuthServ.signInWithGoogle(): FAILURE!")
                        val exception = task.exception
                        if (exception is FirebaseAuthException) {
                            val errorCode = exception.errorCode
                            when (errorCode) {
                                "ERROR_INVALID_CUSTOM_TOKEN" -> {
                                    continuation.resume(Response.Error(Event.ERROR_INVALID_CUSTOM_TOKEN.toString()))
                                }

                                "ERROR_CUSTOM_TOKEN_MISMATCH" -> {
                                    continuation.resume(Response.Error(Event.ERROR_CUSTOM_TOKEN_MISMATCH.toString()))
                                }

                                "ERROR_USER_MISMATCH" -> {
                                    continuation.resume(Response.Error(Event.ERROR_USER_MISMATCH.toString()))
                                }

                                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                    continuation.resume(Response.Error(Event.ERROR_EMAIL_ALREADY_IN_USE.toString()))
                                }

                                else -> {
                                    continuation.resume(Response.Error(Event.ERROR_UNKNOWN.toString()))
                                }
                            }
                        } else {
                            if (exception != null) {
                                Log.d(MY_TAG, "FirebaseAuthServ.SingIn()-Exception: " + exception.message)
                            }
                            continuation.resume(Response.Error(Event.ERROR_UNKNOWN.toString()))
                        }
                    }
                }
        }
    }

    override suspend fun sendEmailVerification(): Event {
        return suspendCancellableCoroutine { continuation ->
            val user = auth.currentUser

            if (user != null) {
                try {
                    user.sendEmailVerification()
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            auth.signOut()
                            Log.d(MY_TAG, "FirebaseAuthServ.sendEmailVer(): SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(MY_TAG, "FirebaseAuthServ.sendEmailVer: FAILURE!\nMessage: ${exception.message}")
                        }
                } catch (e: Exception) {
                    Log.d(MY_TAG, "FirebaseAuthServ.sendEmailVer-Exception: ${e.message}")
                }
            }
        }
    }

    override fun getGoogleSignInIntent(context:Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile()
            .requestIdToken(context.getString(R.string.default_web_client_id)).build()

        signInClient = GoogleSignIn.getClient(context, gso)

        return signInClient.signInIntent
    }

    override suspend fun rollBackRegister(): Event {
        return suspendCancellableCoroutine { continuation ->
            val user = auth.currentUser
            if (user != null) {
                try {
                    user.delete()
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            Log.d(MY_TAG, "FirebaseAuthServ.rollBackReg: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(MY_TAG, "FirebaseAuthServ.rollBackReg: FAILURE!\nMessage: ${exception.message}")
                        }
                } catch (e: Exception) {
                    Log.d(MY_TAG, "FirebaseAuthServ.rollBackReg-Exception: ${e.message}")
                }
            }
        }
    }

}