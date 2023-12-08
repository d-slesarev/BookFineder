package ua.khai.slesarev.bookfinder.util.AccountHelper

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivity

class FirebaseAccHelper(private val activity: SingInActivity) : AccountHelper {
    override fun signUpWithEmailPassword(email: String,password: String,username: String): Response {
        lateinit var result: Response

        if (email.isEmpty() && password.isEmpty() && username.isEmpty()) {
            result = Response.ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME
        } else {
            if (email.isEmpty() && password.isEmpty()) {
                result = Response.ERROR_MISSING_EMAIL_AND_PASSWORD
            } else {
                if (email.isEmpty() && username.isEmpty()) {
                    result = Response.ERROR_MISSING_EMAIL_AND_NAME
                } else {
                    if (username.isEmpty() && password.isEmpty()) {
                        result = Response.ERROR_MISSING_NAME_AND_PASSWORD
                    } else {
                        result = if (email.isNotEmpty()) {
                            if (password.isNotEmpty()) {
                                if (username.isNotEmpty()) {
                                    performSignUpTransaction(email, password, username)
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

        return result
    }

    private fun performSignUpTransaction(email: String, password: String, username: String): Response {
        lateinit var result: Response
        activity.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task: Task<AuthResult> ->

                if (task.isSuccessful) {
                    val user = task.result?.user!!

                    if (addUserToDatabase(user?.uid, email, username) == Response.SUCCESS) {
                        if (sendEmailVerification(user) == Response.SUCCESS) {
                            result = Response.SUCCESS
                        } else {
                            if (rollBackAddUser(user?.uid) == Response.SUCCESS) {
                                result = Response.ERROR_UNKNOWN
                            } else {
                                result = Response.SERVER_ERROR
                            }

                            if (rollBackRegister(user) == Response.SUCCESS) {
                                result = Response.ERROR_UNKNOWN
                            } else {
                                result = Response.SERVER_ERROR
                            }
                        }
                    } else {
                        if (rollBackRegister(user) == Response.SUCCESS) {
                            result = Response.ERROR_UNKNOWN
                        } else {
                            result = Response.SERVER_ERROR
                        }
                    }

                    activity.auth.signOut()

                } else {
                    val exception = task.exception as FirebaseAuthException
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
                }
            }

        return result
    }

    fun sendEmailVerification(user: FirebaseUser): Response {
        lateinit var result: Response

        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = Response.SUCCESS
            } else {
                result = Response.ERROR_UNKNOWN
            }
        }

        return result
    }

    fun addUserToDatabase(uid: String?, email: String?, username: String): Response {
        lateinit var result: Response

        uid?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(it)
            val newUser = User(username, email!!)
            databaseReference.setValue(newUser).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = Response.SUCCESS
                } else {
                    result = Response.ERROR_UNKNOWN
                }
            }
        }
        return result
    }

    fun rollBackAddUser(uid: String?): Response {
        lateinit var result: Response

        uid?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(it)
            databaseReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = Response.SUCCESS
                } else {
                    result = Response.SERVER_ERROR
                }
            }
        }
        return result
    }

    fun rollBackRegister(user: FirebaseUser): Response {
        lateinit var result: Response

        user?.delete()
            ?.addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {
                    result = Response.SUCCESS
                } else {
                    result = Response.SERVER_ERROR
                }
            }

        return result
    }
}
