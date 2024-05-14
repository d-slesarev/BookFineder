package ua.khai.slesarev.bookfinder.data.remote.api.google_books

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import ua.khai.slesarev.bookfinder.data.model.BookShelvesResponse

interface GoogleBooksAPI {
    @GET("books/v1/volumes")
    fun getBooks(@Query("q") query: String): Call<BookShelvesResponse>

    @GET("books/v1/mylibrary/bookshelves/{shelf}/volumes")
    fun getBooksShelves(
        @Header("Authorization") authorization: String,
        @Path("shelf") shelfId: Int = 7,
        @Query("key") apiKey: String,
        @Query("fields") fields: String = "items(volumeInfo(title,authors,imageLinks(thumbnail)))"
    ): Call<BookShelvesResponse>

    @GET("books/v1/volumes")
    fun getNewestEbook(
        @Query("key") apiKey: String,
        @Query("q") query: String = "ebook",
        @Query("orderBy") orderBy: String = "newest",
        @Query("maxResults") maxResults: Int = 20,
        @Query("fields") fields: String = "items(volumeInfo(title,authors,imageLinks(thumbnail)))",
        @Query("filter") filter: String = "partial"
    ): Call<BookShelvesResponse>

    @GET("books/v1/volumes")
    fun getGenreList(
        @Query("q") genreQuery: String,
        @Query("key") apiKey: String,
        @Query("fields") fields: String = "items(volumeInfo(title,authors,imageLinks(thumbnail)))",
        @Query("maxResults") maxResults: Int = 20,
        @Query("filter") filter: String = "partial"
    ): Call<BookShelvesResponse>
}