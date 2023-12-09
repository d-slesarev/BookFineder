package ua.khai.slesarev.bookfinder.util.AccountHelper

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp.UiState

class FirebaseAccHelper() : AccountHelper {

    private val TAG = "FirebaseAuth"
    private var auth: FirebaseAuth = Firebase.auth
    private var database: FirebaseDatabase = FirebaseDatabase
        .getInstance("https://book-finder-2b1d4-default-rtdb.europe-west1.firebasedatabase.app/")

    fun emptyCheck(email: String, password: String, username: String): Response {

        if (email.isEmpty() && password.isEmpty() && username.isEmpty()) {
            return Response.ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME
        } else {
            if (email.isEmpty() && password.isEmpty()) {
                return Response.ERROR_MISSING_EMAIL_AND_PASSWORD
            } else {
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

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        username: String
    ): Response {
        var result: Response = Response.DEFAULT

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail: SUCCESS")
                     Response.SUCCESS
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        val errorCode = exception.errorCode
                        when (errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                result = Response.ERROR_INVALID_EMAIL
                            }

                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                result = Response.ERROR_EMAIL_ALREADY_IN_USE
                            }

                            "ERROR_INVALID_CREDENTIAL" -> {
                                result = Response.ERROR_INVALID_CREDENTIAL
                            }

                            "ERROR_WEAK_PASSWORD" -> {
                                result = Response.ERROR_WEAK_PASSWORD
                            }

                            else -> {
                                result = Response.ERROR_UNKNOWN
                            }
                        }
                    } else {
                        if (exception != null) {
                            Log.d(TAG, "sendEmailVerification: " + exception.message)
                        }
                        result = Response.ERROR_UNKNOWN
                    }
                }
            }

        return result
    }

    suspend fun sendEmailVerification(): Response {
        var result: Response = Response.DEFAULT
        val user = auth.currentUser

        if (user != null) {
            try {
                user.sendEmailVerification()
                    .addOnSuccessListener {
                        result = Response.SUCCESS
                        Log.d(TAG, "sendEmailVerification: SUCCESS")
                    }
                    .addOnFailureListener { exception ->
                        result = Response.ERROR_UNKNOWN
                        Log.d(TAG, "sendEmailVerification: " + exception.message)
                    }
            } catch (e: Exception) {
                Log.d(TAG, "Exception: " + e.message)
            }
        }

        return result

    }

    suspend fun addUserToDatabase(email: String, username: String): Response {
        var result: Response = Response.DEFAULT
        val uid = auth.currentUser?.uid

        if (uid != null) {
            try {
                val databaseReference = database
                    .getReference(uid)

                val newUser = User(uid, username, email)
                databaseReference.setValue(newUser)
                    .addOnSuccessListener {
                        result = Response.SUCCESS
                        Log.d(TAG, "addUserToDatabase: SUCCESS")
                    }
                    .addOnFailureListener { exception ->
                        result = Response.ERROR_UNKNOWN
                        Log.d(TAG, "addUserToDatabase: " + exception.message)
                    }
            } catch (e: Exception) {
                Log.d(TAG, "Exception: " + e.message)
            }
        }

        return result
    }

    suspend fun rollBackAddUser(): Response {
        var result: Response = Response.DEFAULT
        val uid = auth.currentUser?.uid

        return withContext(Dispatchers.IO) {
            if (uid != null) {
                try {
                    val databaseReference = database
                        .getReference(uid)

                    databaseReference.removeValue()
                        .addOnSuccessListener {
                            result = Response.SUCCESS
                            Log.d(TAG, "rollBackAddUser: SUCCESS")
                        }
                        .addOnFailureListener { exception ->
                            result = Response.ERROR_UNKNOWN
                            Log.d(TAG, "rollBackAddUser: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }

            return@withContext result
        }

    }

    suspend fun rollBackRegister(): Response {
        var result: Response = Response.DEFAULT
        val user = auth.currentUser

        return withContext(Dispatchers.IO) {
            if (user != null) {
                try {
                    user.delete()
                        .addOnSuccessListener {
                            result = Response.SUCCESS
                            Log.d(TAG, "rollBackRegister: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            result = Response.ERROR_UNKNOWN
                            Log.d(TAG, "rollBackRegister: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }

            return@withContext result
        }

    }
}

