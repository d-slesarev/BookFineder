package ua.khai.slesarev.bookfinder.ui.sign_in_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.appoauth.AuthStateManager
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.ui.home_screen.HomeActivity

class SingInActivity: AppCompatActivity() {
    private val authState: AuthStateManager = AuthStateManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BookFinder)
        super.onCreate(savedInstanceState)

        if (authState.isLoggedIn()) {
            Log.d(MY_TAG, "SingInActivity.startActivity: Activated!")
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
        setContentView(R.layout.activity_sing_in)
    }
}