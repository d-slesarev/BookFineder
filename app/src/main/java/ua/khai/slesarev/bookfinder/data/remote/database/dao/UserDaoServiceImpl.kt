package ua.khai.slesarev.bookfinder.data.remote.database.dao

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import ua.khai.slesarev.bookfinder.data.models.remote.user.UserRemote
import ua.khai.slesarev.bookfinder.data.remote.database.service.UserDaoService
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.data.util.URL_DATABASE
import kotlin.coroutines.resume

class UserDaoServiceImpl : UserDaoService {

    private var auth: FirebaseAuth = Firebase.auth
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(URL_DATABASE)
    override suspend fun saveUser(user: UserRemote): Event {
        return suspendCancellableCoroutine { continuation ->
            val uid = auth.currentUser?.uid

            if (uid != null) {
                try {
                    val databaseReference = database
                        .getReference(uid)

                    databaseReference.setValue(user)
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            Log.d(MY_TAG, "UserDaoServ.saveUser: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(
                                MY_TAG,
                                "UserDaoServ.saveUser: FAILURE!\nMessage: ${exception.message}"
                            )
                        }
                } catch (e: Exception) {
                    Log.d(MY_TAG, "UserDaoServ.saveUser-Exception: ${e.message}")
                }
            }
        }

    }

    override suspend fun loadUserByID(uid: String): Response<UserRemote> {

        val databaseReference = database.getReference(uid)

        var userEmail:String = suspendCancellableCoroutine { continuation ->
            try {
                val emailRef = databaseReference.child("email")

                emailRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val email = snapshot.value as String?
                        if (email != null) {
                            continuation.resume(email)
                            Log.d(MY_TAG, "UserDaoServ.loadUserByID: SUCCESS!")
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume("")
                        Log.d(MY_TAG, "UserDaoServ.loadUserByID: FAILURE!\nMessage: ${error.message}")
                    }
                })
            } catch (e: Exception) {
                Log.d(MY_TAG, "UserDaoServ.loadUserByID-Exception: ${e.message}")
            }
        }

        var userName:String = suspendCancellableCoroutine { continuation ->
            try {
                val usernameRef = databaseReference.child("username")

                usernameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.value as String?
                        if (name != null) {
                            continuation.resume(name)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume("")
                        Log.d(MY_TAG, "UserDaoServ.loadUserByID: FAILURE!\nMessage: ${error.message}")
                    }
                })
            } catch (e: Exception) {
                Log.d(MY_TAG, "UserDaoServ.loadUserByID-Exception: ${e.message}")
            }
        }

        if (userEmail.isNotEmpty() && userName.isNotEmpty()){
            return Response.Success(UserRemote(userName, userEmail))
        } else {
            return Response.Error(Event.FAILURE.toString())
        }
    }

    override suspend fun updateUser(user: UserRemote): Event {
        return suspendCancellableCoroutine { continuation ->
            val uid = auth.currentUser?.uid

            if (uid != null) {
                try {
                    val databaseReference = database
                        .getReference(uid)

                    databaseReference.setValue(user)
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            Log.d(MY_TAG, "UserDaoServ.updateUser: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(
                                MY_TAG,
                                "UserDaoServ.updateUser: FAILURE!\nMessage: ${exception.message}"
                            )
                        }
                } catch (e: Exception) {
                    Log.d(MY_TAG, "UserDaoServ.updateUser-Exception: ${e.message}")
                }
            }
        }
    }

    override suspend fun deleteUserByID(uid: String): Event {
        return suspendCancellableCoroutine { continuation ->
            if (uid != null) {
                try {
                    val databaseReference = database
                        .getReference(uid)

                    databaseReference.removeValue()
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            Log.d(MY_TAG, "UserDaoServ.deleteUserByID: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(
                                MY_TAG,
                                "UserDaoServ.deleteUserByID: FAILURE!\nMessage: ${exception.message}"
                            )
                        }
                } catch (e: Exception) {
                    Log.d(MY_TAG, "UserDaoServ.deleteUserByID-Exception: ${e.message}")
                }
            }
        }
    }

}