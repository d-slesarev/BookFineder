package ua.khai.slesarev.bookfinder.ui.sign_in_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class SingInActivityViewModel(application: Application) : AndroidViewModel(application) {

}