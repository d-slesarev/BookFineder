package ua.khai.slesarev.bookfinder.ui.util.AccountHelper

import ua.khai.slesarev.bookfinder.data.util.Event

interface AccountHelper {

    suspend fun signUpWithEmailPassword(email:String, password:String, username: String): Event
    suspend fun signInWithEmailPassword(email:String, password:String): Event
}