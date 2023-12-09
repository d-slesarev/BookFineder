package ua.khai.slesarev.bookfinder.util.AccountHelper

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivity

interface AccountHelper {

    suspend fun signUpWithEmailPassword(email:String, password:String, username: String):Response
}