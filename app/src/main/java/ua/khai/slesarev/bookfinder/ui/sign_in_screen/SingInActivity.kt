package ua.khai.slesarev.bookfinder.ui.sign_in_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.AuthService
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.impl.FirebaseAuthService
import ua.khai.slesarev.bookfinder.data.util.TAG
import ua.khai.slesarev.bookfinder.ui.home_screen.HomeActivity
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.HomeActivityViewModel
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn.SignInViewModel

class SingInActivity : AppCompatActivity() {

    private val viewModel: SingInActivityViewModel by viewModels()
    private var auth: FirebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BookFinder)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)

        if (auth.currentUser != null) {
            Log.d(TAG, "SingInActivity.startActivity: Activated!")
            val intent = Intent(this, HomeActivity::class.java)

            lifecycleScope.launch {
                viewModel.updateUI()
                val bundle = Bundle().apply {

                    putString("userName", viewModel.userName)
                    putString("userEmail", viewModel.userEmail)
                }

                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            }
        }
    }
}