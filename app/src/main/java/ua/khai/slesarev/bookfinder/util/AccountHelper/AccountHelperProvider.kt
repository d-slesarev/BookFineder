package ua.khai.slesarev.bookfinder.util.AccountHelper

class AccountHelperProvider {
    private lateinit var firebaseAccHelper:FirebaseAccHelper

    fun getAccountHelper():AccountHelper{
        return firebaseAccHelper
    }
}