package ua.khai.slesarev.bookfinder.util.AccountHelper

interface AccountHelper {

    fun signUpWithEmailPassword(email:String, password:String, username: String):Response
}