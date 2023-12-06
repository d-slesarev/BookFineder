package ua.khai.slesarev.bookfinder.util.AccountHelper

class AccountHelperProvider {
    private val firebaseAccHelper = FirebaseAccHelper()
        get() = field

    fun getAccountHelper():AccountHelper{
        return firebaseAccHelper
    }
}