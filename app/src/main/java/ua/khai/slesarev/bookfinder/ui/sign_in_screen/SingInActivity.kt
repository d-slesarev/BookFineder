package ua.khai.slesarev.bookfinder.ui.sign_in_screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.databinding.FragSingUpBinding
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp.SignUpViewModel
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp.SignUpViewModelFactory
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp.SingUp
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn.SingIn
import ua.khai.slesarev.bookfinder.util.AccountHelper.FirebaseAccHelper
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response

class SingInActivity : AppCompatActivity() {

    private lateinit var viewModel: SignUpViewModel
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BookFinder)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)

        auth = Firebase.auth

    }

    fun getFirebaseAuth(): FirebaseAuth {
        return auth
    }

    fun getSingInActivity(): SingInActivity {
        return this
    }
}