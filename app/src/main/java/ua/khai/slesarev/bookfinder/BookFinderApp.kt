package ua.khai.slesarev.bookfinder

import android.app.Application
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.appoauth.AuthStateManager

class BookFinderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthStateManager.init(this)
    }
}