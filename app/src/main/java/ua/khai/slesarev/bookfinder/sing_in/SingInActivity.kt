package ua.khai.slesarev.bookfinder.sing_in

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.FragSingInBinding

class SingInActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_BookFinder)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)

        /*
        Handler().postDelayed({
            //startActivity(Intent(this, SingInActivity::class.java))

            val intent = Intent(this, SingInActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_in)
            startActivity(intent, options.toBundle())

            finish()
        }, 2000)*/
    }
}