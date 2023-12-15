package ua.khai.slesarev.bookfinder

import android.app.Application
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivityViewModel

class BookFinderApp : Application() {


}