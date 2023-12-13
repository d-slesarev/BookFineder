package ua.khai.slesarev.bookfinder.ui.sign_in_screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ua.khai.slesarev.bookfinder.R

class SingInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BookFinder)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)
    }

}