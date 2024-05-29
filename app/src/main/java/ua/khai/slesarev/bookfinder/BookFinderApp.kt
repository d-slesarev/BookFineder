package ua.khai.slesarev.bookfinder

import android.app.Application
import android.util.Log
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.appoauth.AuthStateManager
import ua.khai.slesarev.bookfinder.data.util.DATABASE_NAME
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class BookFinderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        AuthStateManager.init(this)
        BookFinderDatabase.init(this)
    }

    companion object {
        lateinit var instance: BookFinderApp
            private set
    }
}