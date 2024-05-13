package ua.khai.slesarev.bookfinder.data.util

import net.openid.appauth.ResponseTypeValues

class AuthConfig {
    companion object {
        val SHARED_PREFERENCES_NAME = "AUTH_STATE_PREFERENCE"
        val AUTH_STATE = "AUTH_STATE"

        val SCOPE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile"
        val SCOPE_EMAIL = "email"
        val SCOPE_OPENID = "openid"
        val SCOPE_BOOKS = "https://www.googleapis.com/auth/books"

        val CLIENT_ID = "292942742398-l5the5mgslvtqi7vquig0v05300k905g.apps.googleusercontent.com"

        val RESPONSE_TYPE = ResponseTypeValues.CODE

        val URL_AUTHORIZATION = "https://accounts.google.com/o/oauth2/v2/auth"
        val URL_TOKEN_EXCHANGE = "https://www.googleapis.com/oauth2/v4/token"

        val URL_AUTH_REDIRECT = "ua.khai.slesarev.bookfinder:/oauth2callback"
        val URL_LOGOUT = "https://accounts.google.com/o/oauth2/revoke?token="
    }
}