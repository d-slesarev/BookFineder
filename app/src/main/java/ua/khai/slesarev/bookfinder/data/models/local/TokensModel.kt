package ua.khai.slesarev.bookfinder.data.models.local

data class TokensModel(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)
