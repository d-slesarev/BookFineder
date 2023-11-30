package ua.khai.slesarev.bookfinder.sign_in_screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ua.khai.slesarev.bookfinder.R

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