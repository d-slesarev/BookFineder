package ua.khai.slesarev.bookfinder.util.AccountHelper

class AccountHelperProvider {
    private val firebaseAccHelper:FirebaseAccHelper = FirebaseAccHelper()

    fun getAccountHelper():AccountHelper{
        return firebaseAccHelper
    }
}