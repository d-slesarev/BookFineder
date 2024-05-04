package ua.khai.slesarev.bookfinder.data.remote.api.user_profile

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import ua.khai.slesarev.bookfinder.data.model.Person
import ua.khai.slesarev.bookfinder.data.model.User

// TODO: Реализовать сервис доступа к данным профиля аккаунта пользователя
interface GooglePeopleAPI {
    @GET("https://people.googleapis.com/v1/people/me?personFields=names,emailAddresses,photos&fields=names/displayName,photos/url,emailAddresses/value")
    fun getUserInfo(
        @Header("Authorization") accessToken: String
    ): Call<Person>
}