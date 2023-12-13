package ua.khai.slesarev.bookfinder.ui.util.AccountHelper

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import ua.khai.slesarev.bookfinder.data.model.UserRemote
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.URL_DATABASE
import kotlin.coroutines.resume

class FirebaseAccHelper() {

    private val TAG = "FirebaseAuth"
    private var auth: FirebaseAuth = Firebase.auth
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(URL_DATABASE)

    private var userName:String = ""
        get() = userName
    private var userEmail:String = ""
        get() = userEmail

    fun signUpEmptyCheck(email: String, password: String, username: String): Event {

        if (email.isEmpty() && password.isEmpty() && username.isEmpty()) {
            return Event.ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME
        } else {
            if (email.isEmpty() && password.isEmpty()) {
                return Event.ERROR_MISSING_EMAIL_AND_PASSWORD
            }
            else {
                if (email.isEmpty() && username.isEmpty()) {
                    return Event.ERROR_MISSING_EMAIL_AND_NAME
                } else {
                    if (username.isEmpty() && password.isEmpty()) {
                        return Event.ERROR_MISSING_NAME_AND_PASSWORD
                    } else {
                        return if (email.isNotEmpty()) {
                            if (password.isNotEmpty()) {
                                if (username.isNotEmpty()) {
                                    Event.SUCCESS
                                } else {
                                    Event.ERROR_MISSING_NAME
                                }
                            } else {
                                Event.ERROR_MISSING_PASSWORD
                            }
                        } else {
                            Event.ERROR_MISSING_EMAIL
                        }
                    }
                }
            }
        }
    }

    fun signInEmptyCheck(email: String, password: String): Event {
        return if (email.isEmpty() && password.isEmpty()) {
            Event.ERROR_MISSING_EMAIL_AND_PASSWORD
        }
        else {
            if (email.isNotEmpty()) {
                return if (password.isNotEmpty()) {
                    Event.SUCCESS
                } else {
                    Event.ERROR_MISSING_PASSWORD
                }
            } else {
                return Event.ERROR_MISSING_EMAIL
            }
        }
    }


    suspend fun addUserToDatabase(email: String, username: String): Event {
        return suspendCancellableCoroutine { continuation ->
            val uid = auth.currentUser?.uid

            if (uid != null) {
                try {
                    val databaseReference = database
                        .getReference(uid)

                    val newUser = UserRemote(uid, username)

                    databaseReference.child(uid).child("username")

                    databaseReference.setValue(newUser)
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            Log.d(TAG, "addUserToDatabase: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(TAG, "addUserToDatabase: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }

        }
    }
}

