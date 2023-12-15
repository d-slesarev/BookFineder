package ua.khai.slesarev.bookfinder.ui.home_screen.fragments

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

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var localDatabaseApp: BookFinderDatabase = BookFinderDatabase.getInstance(application)
    private var localDao: UserDao = localDatabaseApp.userDao()
    private var auth: FirebaseAuth = Firebase.auth

    var userEmail: String = ""
    var userName: String = ""

    fun singOut() {
        auth.signOut()
    }

    suspend fun updateUI() {
        try {
            withContext(Dispatchers.IO) {
                val user = localDao.getUserByID(auth.currentUser?.uid!!)

                userName = user.username
                userEmail = user.email
            }
        } catch (e: Exception) {
            Log.d(MY_TAG, "SingInActViewModel.updateUI-Exception: ${e.message}")
        }
    }
}