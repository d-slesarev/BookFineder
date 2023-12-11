package ua.khai.slesarev.bookfinder.util.AccountHelper

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import ua.khai.slesarev.bookfinder.data.model.User
import kotlin.coroutines.resume

class FirebaseAccHelper() : AccountHelper {

    private val TAG = "FirebaseAuth"
    private val URL_DATABASE = "https://book-finder-2b1d4-default-rtdb.europe-west1.firebasedatabase.app/"
    private var auth: FirebaseAuth = Firebase.auth
    private var database: FirebaseDatabase = FirebaseDatabase
        .getInstance(URL_DATABASE)

    private var userName:String = ""
        get() = userName
    private var userEmail:String = ""
        get() = userEmail

    fun signUpEmptyCheck(email: String, password: String, username: String): Response {

        if (email.isEmpty() && password.isEmpty() && username.isEmpty()) {
            return Response.ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME
        } else {
            if (email.isEmpty() && password.isEmpty()) {
                return Response.ERROR_MISSING_EMAIL_AND_PASSWORD
            }
            else {
                if (email.isEmpty() && username.isEmpty()) {
                    return Response.ERROR_MISSING_EMAIL_AND_NAME
                } else {
                    if (username.isEmpty() && password.isEmpty()) {
                        return Response.ERROR_MISSING_NAME_AND_PASSWORD
                    } else {
                        return if (email.isNotEmpty()) {
                            if (password.isNotEmpty()) {
                                if (username.isNotEmpty()) {
                                    Response.SUCCESS
                                } else {
                                    Response.ERROR_MISSING_NAME
                                }
                            } else {
                                Response.ERROR_MISSING_PASSWORD
                            }
                        } else {
                            Response.ERROR_MISSING_EMAIL
                        }
                    }
                }
            }
        }
    }

    fun signInEmptyCheck(email: String, password: String):Response {
        return if (email.isEmpty() && password.isEmpty()) {
            Response.ERROR_MISSING_EMAIL_AND_PASSWORD
        }
        else {
            if (email.isNotEmpty()) {
                return if (password.isNotEmpty()) {
                    Response.SUCCESS
                } else {
                    Response.ERROR_MISSING_PASSWORD
                }
            } else {
                return Response.ERROR_MISSING_EMAIL
            }
        }
    }

    override suspend fun signInWithEmailPassword(email: String,password: String): Response {
        return suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Sing In: SUCCESS!")

                        val user = auth.currentUser

                        if (user != null && user.isEmailVerified){
                            continuation.resume(Response.SUCCESS)
                            userEmail = email
                        } else {
                            continuation.resume(Response.ERROR_UNCONFIRMED_EMAIL)
                            auth.signOut()
                        }

                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthException) {
                            val errorCode = exception.errorCode
                            when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    continuation.resume(Response.ERROR_INVALID_EMAIL)
                                }

                                "ERROR_USER_NOT_FOUND" -> {
                                    continuation.resume(Response.ERROR_USER_NOT_FOUND)
                                }

                                "ERROR_INVALID_CREDENTIAL" -> {
                                    continuation.resume(Response.ERROR_INVALID_CREDENTIAL)
                                }

                                else -> {
                                    continuation.resume(Response.ERROR_UNKNOWN)
                                }
                            }
                        } else {
                            if (exception != null) {
                                Log.d(TAG, "Sing In: " + exception.message)
                            }
                            continuation.resume(Response.ERROR_UNKNOWN)
                        }
                    }
                }
        }
    }

    override suspend fun signUpWithEmailPassword(email: String,password: String,username: String): Response {
        return suspendCancellableCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail: SUCCESS!")
                        userName = username
                        continuation.resume(Response.SUCCESS)
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthException) {
                            val errorCode = exception.errorCode
                            when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    continuation.resume(Response.ERROR_INVALID_EMAIL)
                                }

                                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                    continuation.resume(Response.ERROR_EMAIL_ALREADY_IN_USE)
                                }

                                "ERROR_INVALID_CREDENTIAL" -> {
                                    continuation.resume(Response.ERROR_INVALID_CREDENTIAL)
                                }

                                "ERROR_WEAK_PASSWORD" -> {
                                    continuation.resume(Response.ERROR_WEAK_PASSWORD)
                                }

                                else -> {
                                    continuation.resume(Response.ERROR_UNKNOWN)
                                }
                            }
                        } else {
                            if (exception != null) {
                                Log.d(TAG, "sendEmailVerification: " + exception.message)
                            }
                            continuation.resume(Response.ERROR_UNKNOWN)
                        }
                    }
                }
        }
    }

    suspend fun sendEmailVerification(): Response {
        return suspendCancellableCoroutine { continuation ->
            Log.w(TAG, "sendEmailVerification: Started!")
            val user = auth.currentUser

            if (user != null) {
                try {
                    user.sendEmailVerification()
                        .addOnSuccessListener {
                            continuation.resume(Response.SUCCESS)
                            auth.signOut()
                            Log.d(TAG, "sendEmailVerification: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Response.ERROR_UNKNOWN)
                            Log.d(TAG, "sendEmailVerification: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }
            Log.w(TAG, "sendEmailVerification: Finished!")
        }
    }

    suspend fun addUserToDatabase(email: String, username: String): Response {
        return suspendCancellableCoroutine { continuation ->
            Log.w(TAG, "addUserToDatabase: Started!")
            val uid = auth.currentUser?.uid

            if (uid != null) {
                try {
                    val databaseReference = database
                        .getReference(uid)

                    val newUser = User(uid, username, email)

                    databaseReference.child(uid).child("username")

                    databaseReference.setValue(newUser)
                        .addOnSuccessListener {
                            continuation.resume(Response.SUCCESS)
                            Log.d(TAG, "addUserToDatabase: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Response.ERROR_UNKNOWN)
                            Log.d(TAG, "addUserToDatabase: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }
            Log.w(TAG, "addUserToDatabase: Finished!")
        }
    }

    suspend fun rollBackAddUser(): Response {
        return suspendCancellableCoroutine { continuation ->
            val uid = auth.currentUser?.uid
            if (uid != null) {
                try {
                    val databaseReference = database
                        .getReference(uid)

                    databaseReference.removeValue()
                        .addOnSuccessListener {
                            continuation.resume(Response.SUCCESS)
                            Log.d(TAG, "rollBackAddUser: SUCCESS")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Response.ERROR_UNKNOWN)
                            Log.d(TAG, "rollBackAddUser: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }

        }
    }

    suspend fun rollBackRegister(): Response {
        return suspendCancellableCoroutine { continuation ->
            val user = auth.currentUser
            if (user != null) {
                try {
                    user.delete()
                        .addOnSuccessListener {
                            continuation.resume(Response.SUCCESS)
                            Log.d(TAG, "rollBackRegister: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Response.ERROR_UNKNOWN)
                            Log.d(TAG, "rollBackRegister: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }
        }
    }
}

