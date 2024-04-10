package ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth

object TokenStorage {
    var accessToken: String? = null
    var refreshToken: String? = null
    var idToken: String? = null
}