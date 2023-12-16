package ua.khai.slesarev.bookfinder.ui.home_screen

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepository
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var localDatabaseApp: BookFinderDatabase = BookFinderDatabase.getInstance(application)
    private var localDao: UserDao = localDatabaseApp.userDao()
    private var auth: FirebaseAuth = Firebase.auth
    private var authRepo: AuthRepository = AuthRepositoryImpl(application)

    var userEmail: String = ""
    var userName: String = ""
    var userPhoto: Uri = Uri.EMPTY

    suspend fun singOut() {

        try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val user = User(uid = uid, username = userName, email = userEmail, imageUri = "")
                withContext(Dispatchers.IO) {
                    val result = localDao.deleteUser(user)
                    if (result > 0){
                        auth.signOut()
                        authRepo.signOutGoogle()
                        Log.d(MY_TAG, "HomeActVM.singOut: SUCCESS!")
                    } else {
                        auth.signOut()
                        authRepo.signOutGoogle()
                        Log.d(MY_TAG, "HomeActVM.singOut: FAILURE!")
                    }
                }
            } else {
                Log.d(MY_TAG, "HomeActVM.singOut: FAILURE!")
            }
        } catch (e: Exception) {
            Log.d(MY_TAG, "HomeActViewModel.singOut-Exception: ${e.message}")
        }
    }

    suspend fun updateUI() {
        try {
            withContext(Dispatchers.IO) {
                val user = localDao.getUserByID(auth.currentUser?.uid!!)

                userName = user.username
                userEmail = user.email
                userPhoto = Uri.parse(user.imageUri)
            }
        } catch (e: Exception) {
            Log.d(MY_TAG, "HomeActViewModel.updateUI-Exception: ${e.message}")
        }
    }
}