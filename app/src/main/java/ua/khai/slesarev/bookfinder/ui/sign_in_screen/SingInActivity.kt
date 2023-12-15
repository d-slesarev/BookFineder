package ua.khai.slesarev.bookfinder.ui.sign_in_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.ui.home_screen.HomeActivity

class SingInActivity: AppCompatActivity() {

    //private val viewModel: SingInActivityViewModel by viewModels()
    private var auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BookFinder)
        super.onCreate(savedInstanceState)

        if (auth.currentUser != null) {
            Log.d(MY_TAG, "SingInActivity.startActivity: Activated!")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            Log.d(MY_TAG, "SingInActivity.startActivity: Started!")
            finish()
        }
        setContentView(R.layout.activity_sing_in)
    }
}