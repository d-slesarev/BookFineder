package ua.khai.slesarev.bookfinder.data.remote.api.authentication.logout

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface OpenIDLogoutService {
    @GET("o/oauth2/revoke?token=")
    fun endSession(
        @Query("token") accessToken: String,
    ): Call<Void>
}