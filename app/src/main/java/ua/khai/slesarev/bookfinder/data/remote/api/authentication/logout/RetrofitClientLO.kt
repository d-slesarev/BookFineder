package ua.khai.slesarev.bookfinder.data.remote.api.authentication.logout

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.khai.slesarev.bookfinder.data.remote.api.google_books.GoogleBooksAPI

object RetrofitClientLO {
    private const val BASE_URL = "https://accounts.google.com/"

    val instance: OpenIDLogoutService by lazy {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(OpenIDLogoutService::class.java)
    }
}