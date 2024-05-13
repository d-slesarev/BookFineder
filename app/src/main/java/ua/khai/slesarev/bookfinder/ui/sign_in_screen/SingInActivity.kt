package ua.khai.slesarev.bookfinder.ui.sign_in_screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import ua.khai.slesarev.bookfinder.R

class SingInActivity: AppCompatActivity() {
    private var auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BookFinder)
        super.onCreate(savedInstanceState)
/*
        if (auth.currentUser != null) {
            Log.d(MY_TAG, "SingInActivity.startActivity: Activated!")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }*/
        setContentView(R.layout.activity_sing_in)
    }
}