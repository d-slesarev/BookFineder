package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp

import android.app.Application
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivity
import ua.khai.slesarev.bookfinder.util.AccountHelper.AccountHelperProvider
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response

class SignUpViewModel(application: Application, private val activity: SingInActivity) : AndroidViewModel(application){

    private val application = application

    val emailLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()
    val usernameLiveData = MutableLiveData<String>()

    val emailColorLiveData = MutableLiveData<Int>()
    val passwordColorLiveData = MutableLiveData<Int>()
    val usernameColorLiveData = MutableLiveData<Int>()

    @RequiresApi(Build.VERSION_CODES.P)
    fun signUpWithEmailPassword(email: String, password: String, username: String) {
        lateinit var result: Response

        try {
            result = signUpWithEmailPasswordInternal(email, password, username)
        } catch (e:Exception) {
            throw Exception("Registration failed: ${e.message}")
        }

        when (result) {

            Response.ERROR_INVALID_EMAIL -> {
                emailLiveData.value = "Incorrect e-mail"
                passwordLiveData.value = "Not checked"
                usernameLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
            }

            Response.ERROR_EMAIL_ALREADY_IN_USE -> {
                emailLiveData.value = "This e-mail is already in use"
                passwordLiveData.value = "Not checked"
                usernameLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
            }

            Response.ERROR_INVALID_CREDENTIAL -> {
                emailLiveData.value = "The e-mail or password is incorrect"
                passwordLiveData.value = "The e-mail or password is incorrect"
                usernameLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
            }

            Response.ERROR_WEAK_PASSWORD -> {
                passwordLiveData.value = "Password is too simple or short"
                emailLiveData.value = "Not checked"
                usernameLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
            }

            Response.ERROR_MISSING_NAME -> {
                passwordLiveData.value = "Not checked"
                emailLiveData.value = "Not checked"
                usernameLiveData.value = "Required"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
            }

            Response.ERROR_MISSING_EMAIL -> {
                passwordLiveData.value = "Not checked"
                emailLiveData.value = "Required"
                usernameLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
            }

            Response.ERROR_MISSING_PASSWORD -> {
                passwordLiveData.value = "Required"
                emailLiveData.value = "Not checked"
                usernameLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
            }

            Response.ERROR_MISSING_EMAIL_AND_PASSWORD -> {
                emailLiveData.value = "Required"
                passwordLiveData.value = "Required"
                usernameLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
            }

            Response.ERROR_MISSING_EMAIL_AND_NAME -> {
                emailLiveData.value = "Required"
                usernameLiveData.value = "Required"
                passwordLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
            }

            Response.ERROR_MISSING_NAME_AND_PASSWORD -> {
                usernameLiveData.value = "Required"
                passwordLiveData.value = "Required"
                emailLiveData.value = "Not checked"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.RED)
            }

            Response.ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME -> {
                emailLiveData.value = "Required"
                passwordLiveData.value = "Required"
                usernameLiveData.value = "Required"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.BROWN)
            }

            Response.ERROR_UNKNOWN -> {
                MaterialAlertDialogBuilder(getApplication())
                    .setTitle("Unknown error")
                    .setMessage("This may be a temporary problem, please try again later.")
                    .setPositiveButton("OK") { dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }.show()
                emailLiveData.value = "Correctly"
                passwordLiveData.value = "Correctly"
                usernameLiveData.value = "Correctly"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)
            }

            Response.SERVER_ERROR -> {
                MaterialAlertDialogBuilder(getApplication())
                    .setTitle("Server error")
                    .setMessage("Please contact the support center with your problem.")
                    .setPositiveButton("OK") { dialog, which ->
                        // Обработка нажатия кнопки "Подтвердить"
                    }.show()
                emailLiveData.value = "Correctly"
                passwordLiveData.value = "Correctly"
                usernameLiveData.value = "Correctly"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)
            }

            else -> {
                MaterialAlertDialogBuilder(application)
                    .setTitle("Confirm your e-mail")
                    .setMessage("We've sent you a link to confirm your email. Please go to your email and follow the confirmation link.")
                    .setPositiveButton("OK") { dialog, which ->
                        // navController.navigate(R.id.action_singUp_to_singIn)
                    }.show()
                emailLiveData.value = "Correctly"
                passwordLiveData.value = "Correctly"
                usernameLiveData.value = "Correctly"

                emailColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)
                passwordColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)
                usernameColorLiveData.value = ContextCompat.getColor(application, R.color.GREEN)

            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun signUpWithEmailPasswordInternal(email: String, password: String, username: String): Response {
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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun performSignUpTransaction(email: String, password: String, username: String): Response {
        lateinit var result: Response
        activity.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(application.mainExecutor) { task: Task<AuthResult> ->

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