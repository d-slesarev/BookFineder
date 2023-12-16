package ua.khai.slesarev.bookfinder.data.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import ua.khai.slesarev.bookfinder.R

const val DATABASE_NAME = "book-finder-db"
const val MY_TAG = "DATA_LOG"
const val URL_DATABASE = "https://book-finder-2b1d4-default-rtdb.europe-west1.firebasedatabase.app/"
const val GOOGLE_REQUEST_CODE = 132

fun getResourceUri(context: Context, drawableId: Int): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(drawableId))
        .appendPath(context.resources.getResourceTypeName(drawableId))
        .appendPath(context.resources.getResourceEntryName(drawableId))
        .build()
}

fun getDefaultProfileImage(context: Context): Uri {
    return getResourceUri(context, R.drawable.ic_account_24)
}