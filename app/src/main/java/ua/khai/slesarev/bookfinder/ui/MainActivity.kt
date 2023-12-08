package ua.khai.slesarev.bookfinder.ui

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.databinding.ActivityMainBinding
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    private val TAG = "FirebaseAuth"
    lateinit var createAccountBtn: Button
    lateinit var nameTexInp: TextInputEditText
    lateinit var emailRegisterTexInp: TextInputEditText
    lateinit var passRegisterTexImp: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance("https://book-finder-2b1d4-default-rtdb.europe-west1.firebasedatabase.app/")

        createAccountBtn = findViewById(R.id.createAccountBtn2)
        nameTexInp = findViewById(R.id.nameTexInp2)
        emailRegisterTexInp = findViewById(R.id.emailRegisterTexInp2)
        passRegisterTexImp = findViewById(R.id.passRegisterTexImp2)

        createAccountBtn.setOnClickListener {
            lateinit var result: Response
            var username = nameTexInp.text.toString()
            //var email = emailRegisterTexInp.text.toString()
            //var password = passRegisterTexImp.text.toString()

            var email = "davidslesarev610@gmail.com"
            var password = "8sj95uEKC7"

            try {
                result = performSignUpTransaction(email, password, username)
                Log.d(TAG, "Final result: " + result.toString())
            } catch (e: Exception) {
                Log.d(TAG, "Exception: " + e.message)
            }

        }

    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail: SUCCESS")
                    Toast.makeText(
                        baseContext,
                        "Authentication SUCCESS.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail: FAILURE", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        // [END create_user_with_email]
    }

    private fun performSignUpTransaction(email: String, password: String, username: String): Response {
        var result: Response = Response.DEFAULT

        if (isNetworkAvailable()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        Log.d(TAG, "createUserWithEmail: SUCCESS")
                        Toast.makeText(
                            baseContext,
                            "Authentication SUCCESS.",
                            Toast.LENGTH_SHORT,
                        ).show()

                        var output:Response
                        val user = task.result?.user!!
                        // val resultDeferred: Deferred<Response> =
                        CoroutineScope(Dispatchers.Main).launch {
                            output = addUserToDatabase(user.uid, email, username)

                            if (output == Response.SUCCESS) {

                                output = sendEmailVerification(user)

                                if (output == Response.SUCCESS) {
                                     Response.SUCCESS
                                }
                                else {
                                     Response.ERROR_UNKNOWN
                                }
                            }
                            else {
                                 Response.ERROR_UNKNOWN
                            }
                        } // The End CoroutineScope
                    }
                    else {

                        Log.w(TAG, "createUserWithEmail: FAILURE", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()


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
        }
        else {
            result = Response.NETWORK_ERROR
        }

        return result
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun sendEmailVerification(user: FirebaseUser): Response {
        var result: Response = Response.DEFAULT

        try {
            user.sendEmailVerification()
                .addOnSuccessListener {
                    result = Response.SUCCESS
                    Log.d(TAG, "sendEmailVerification: SUCCESS")
                }
                .addOnFailureListener {exception ->
                    result = Response.ERROR_UNKNOWN
                    Log.d(TAG, "sendEmailVerification: " + exception.message)
                }
        } catch (e: Exception) {
            Log.d(TAG, "Exception: " + e.message)
        }

        return result
    }

    private suspend fun addUserToDatabase(uid: String, email: String, username: String): Response {

        return withContext(Dispatchers.IO) {

            val deferred = async {
                // Добавление нового пользователя в базу данных
                val databaseReference = database
                    .getReference(uid)

                val newUser = User(uid, username, email)

                databaseReference.setValue(newUser)
                    .addOnSuccessListener {
                        Log.d(TAG, "addUserToDatabase(in async): SUCCESS")
                    }
                    .addOnFailureListener {exception ->
                        Log.d(TAG, "addUserToDatabase.Exception: " + exception.message)
                    }
            }

            try {
                // Ожидание завершения операции
                deferred.await()
                Response.SUCCESS
            } catch (e: Exception) {
                // Обработка ошибки при добавлении пользователя
                Log.d(TAG, "addUserToDatabase.Exception: " + e.message)
                Response.ERROR_UNKNOWN
            }

/*            try {
                val databaseReference = database
                    .getReference(uid)

                val newUser = User(uid, username, email)
                databaseReference.setValue(newUser)
                    .addOnSuccessListener {
                        result = Response.SUCCESS
                        Log.d(TAG, "addUserToDatabase: SUCCESS")
                    }
                    .addOnFailureListener {exception ->
                        result = Response.ERROR_UNKNOWN
                        Log.d(TAG, "addUserToDatabase: " + exception.message)
                    }
            } catch (e: Exception) {
                Log.d(TAG, "Exception: " + e.message)
            }*/

        }
    }

    private fun rollBackAddUser(uid: String): Response{
        var result: Response = Response.DEFAULT

        try {
            val databaseReference = FirebaseDatabase
                .getInstance("https://book-finder-2b1d4-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference(uid)

            databaseReference.removeValue()
                .addOnSuccessListener {
                    result = Response.SUCCESS
                    Log.d(TAG, "rollBackAddUser: SUCCESS")
                }
                .addOnFailureListener {exception ->
                    result = Response.ERROR_UNKNOWN
                    Log.d(TAG, "rollBackAddUser: " + exception.message)
                }
        } catch (e: Exception) {
            Log.d(TAG, "Exception: " + e.message)
        }
        return result
    }

    private fun rollBackRegister(user: FirebaseUser): Response {
        var result: Response = Response.DEFAULT

        try {
            user?.delete()
                ?.addOnSuccessListener {
                    result = Response.SUCCESS
                    Log.d(TAG, "rollBackRegister: SUCCESS!")
                }
                ?.addOnFailureListener {exception ->
                    result = Response.ERROR_UNKNOWN
                    Log.d(TAG, "rollBackRegister: " + exception.message)
                }
        } catch (e: Exception) {
            Log.d(TAG, "Exception: " + e.message)
        }

        return result
    }
}
