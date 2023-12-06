package ua.khai.slesarev.bookfinder.util.AccountHelper

import com.google.android.gms.common.internal.ResourceUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import ua.khai.slesarev.bookfinder.data.model.User

class FirebaseAccHelper : AccountHelper {

    val authentication = FirebaseAuth.getInstance()

    override fun signUpWithEmailPassword(email: String, password: String, username: String): Response {
        lateinit var result:Response
        if (email.isNotEmpty() && password.isNotEmpty()){
            authentication.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful){
                    val user = task.result?.user!!
                    if (sendEmailVerification(user).equals(Response.SUCCESS) &&
                        addUserToDatabase(user?.uid, email, username).equals(Response.SUCCESS)
                        ) {
                        result = Response.SUCCESS
                    } else {
                        result = Response.ERROR_UNKNOWN
                    }
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
        }
        return result
    }

    fun sendEmailVerification(user: FirebaseUser): Response {
        lateinit var result:Response

        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful){
                result = Response.SUCCESS
            } else {
                result = Response.ERROR_UNKNOWN
            }
        }

        return result
    }

    fun addUserToDatabase(UID:String?, email: String?, username:String):Response {
        lateinit var result: Response

        UID?.let {
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
}