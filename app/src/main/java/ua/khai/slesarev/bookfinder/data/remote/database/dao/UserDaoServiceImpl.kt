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
import ua.khai.slesarev.bookfinder.data.model.UserRemote
import ua.khai.slesarev.bookfinder.data.remote.database.service.UserDaoService
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.URL_DATABASE
import kotlin.coroutines.resume

class UserDaoServiceImpl : UserDaoService {

    private val TAG = "FirebaseRealtime"
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
                        Log.d(TAG, "addUserToDatabase: SUCCESS!")
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(Event.ERROR_UNKNOWN)
                        Log.d(TAG, "addUserToDatabase: " + exception.message)
                    }
            } catch (e: Exception) {
                Log.d(TAG, "Exception: " + e.message)
            }
        }
    }

    }
    override suspend fun loadUserByID(uid: String): Response<UserRemote> {
        return suspendCancellableCoroutine { continuation ->

            var userEmail = ""
            var userName = ""

            if (uid != null) {
                try {
                    val databaseReference = database.getReference(uid)
                    val usernameRef = databaseReference.child(uid).child("username")
                    val emailRef = databaseReference.child(uid).child("email")

                    usernameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val name = snapshot.value as String?
                            if (name != null) {
                                userName = name
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resume(Response.Error(error.message))
                            Log.d(TAG, "LoadUserError: ${error.message}")
                        }
                    })

                    emailRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val email = snapshot.value as String?
                            if (email != null) {
                                userEmail = email
                                continuation.resume(Response.Success(UserRemote(userName, userEmail)))
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resume(Response.Error(error.message))
                            Log.d(TAG, "LoadUserError: ${error.message}")
                        }
                    })

                } catch (e: Exception) {
                    Log.d(TAG, "LoadUserException: " + e.message)
                }
            }
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
                            Log.d(TAG, "addUserToDatabase: SUCCESS!")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(TAG, "addUserToDatabase: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }
        }
    }
    override suspend fun deleteUser(user: UserRemote): Event {
        return suspendCancellableCoroutine { continuation ->
            val uid = auth.currentUser?.uid
            if (uid != null) {
                try {
                    val databaseReference = database
                        .getReference(uid)

                    databaseReference.removeValue()
                        .addOnSuccessListener {
                            continuation.resume(Event.SUCCESS)
                            Log.d(TAG, "rollBackAddUser: SUCCESS")
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(Event.ERROR_UNKNOWN)
                            Log.d(TAG, "rollBackAddUser: " + exception.message)
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: " + e.message)
                }
            }
        }
    }

}