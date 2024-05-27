package ua.khai.slesarev.bookfinder.data.remote.api.google_books

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.khai.slesarev.bookfinder.data.models.remote.book.BookItem
import ua.khai.slesarev.bookfinder.data.models.remote.book.BookShelvesResponse
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.appoauth.AuthStateManager
import ua.khai.slesarev.bookfinder.data.repository.book.GenreList
import ua.khai.slesarev.bookfinder.data.repository.book.RecentlyViewed
import ua.khai.slesarev.bookfinder.data.repository.book.TypeCall
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import kotlin.coroutines.resume

class BooksService {
    private val api: GoogleBooksAPI = RetrofitClientGB.instance
    private val authState: AuthStateManager = AuthStateManager.getInstance()
    companion object{
        val apiKey = "AIzaSyAnaVw7WeDc4_keRzsSoEJkD7hE616BbWQ"
        var authorizationToken = ""
    }

    suspend fun performGoogleBooksRequest(startIndex: Int, maxResults: Int, typeCall: TypeCall) : Result<List<BookItem>> {
        return withContext(Dispatchers.IO) {
            try {
                when (typeCall) {
                    is RecentlyViewed -> {
                        Log.i(MY_TAG, "getBooksShelves(): Started!")
                        val result = runCatching {
                            // TODO: Доработай функцию получения токена. Что бы не делать постоянно сетевой запрос...
                            val token = authState.refreshAccessToken()
                            authorizationToken = "Bearer $token"
                            Log.i(MY_TAG, "TOKEN:  $token")
                            getBooksShelves(
                                shelfId = typeCall.shelfId,
                                startIndex = startIndex,
                                maxResults = maxResults
                            )
                        }
                        result.getOrElse {
                            Log.e(MY_TAG, "Error fetching access token or bookshelves", it)
                            Result.failure<List<BookItem>>(it)
                        }
                    }
                    is GenreList -> {
                        Log.i(MY_TAG, "getGenreList(): Started!")
                        val genreQuery = typeCall.genreQuery.orEmpty()
                        getGenreList(
                            genreQuery = genreQuery,
                            startIndex = startIndex,
                            maxResults = maxResults
                        )
                    }
                    else -> {
                        Result.failure<List<BookItem>>(Throwable("Unknown API call type"))
                    }
                }
            } catch (e: Exception) {
                Log.e(MY_TAG, "BooksService-Exception: ${e.message}")
                Result.failure(e)
            }
        }
    }
    private suspend fun getBooksShelves(shelfId:Int, startIndex: Int, maxResults: Int) : Result<List<BookItem>> {
        return suspendCancellableCoroutine { continuation ->
            try {
                api.getBooksShelves(
                    authorization = authorizationToken,
                    shelfId = shelfId,
                    apiKey = apiKey,
                    startIndex = startIndex,
                    maxResults = maxResults
                ).enqueue(object : Callback<BookShelvesResponse> {

                    override fun onResponse(call: Call<BookShelvesResponse>, response: Response<BookShelvesResponse>) {
                        if (response.isSuccessful) {
                            Log.i(MY_TAG, "BooksRepository.getBooksShelves: SUCCESS!")
                            val bookShelvesResponse = response.body()
                            if (bookShelvesResponse != null) {
                                continuation.resume(Result.success(bookShelvesResponse.items))
                            }
                        } else {
                            Log.e(MY_TAG, "BooksRepository.getBooksShelves: FAILURE!\nMessage: ${response.message()}")
                            continuation.resume(Result.failure(Throwable(response.errorBody().toString())))
                        }
                    }

                    override fun onFailure(call: Call<BookShelvesResponse>, t: Throwable) {
                        Log.e(MY_TAG, "BooksRepository.getBooksShelves: FAILURE!\n" +
                                "Message: ${t.message}")
                        continuation.resume(Result.failure(t))
                    }
                }).apply {
                    continuation.invokeOnCancellation {
                        NonCancellable.cancel()
                    }
                }
            } catch (e: Exception){
                Log.e(MY_TAG, "BooksService.getBooksShelves-Exception: ${e.message}")
            }
        }
    }
    private suspend fun getGenreList(genreQuery: String, startIndex: Int, maxResults: Int) : Result<List<BookItem>> {
        return suspendCancellableCoroutine { continuation ->
            try {
                api.getGenreList(
                    genreQuery = genreQuery,
                    apiKey = apiKey,
                    startIndex = startIndex,
                    maxResults = maxResults
                ).enqueue(object : Callback<BookShelvesResponse> {

                    override fun onResponse(call: Call<BookShelvesResponse>, response: Response<BookShelvesResponse>) {
                        if (response.isSuccessful) {
                            Log.i(MY_TAG, "BooksRepository.getGenreList: SUCCESS!")
                            val bookShelvesResponse = response.body()
                            if (bookShelvesResponse != null) {
                                continuation.resume(Result.success(bookShelvesResponse.items))
                            }
                        } else {
                            Log.e(MY_TAG, "BooksRepository.getGenreList: FAILURE!")
                            continuation.resume(Result.failure(Throwable(response.errorBody().toString())))
                        }
                    }

                    override fun onFailure(call: Call<BookShelvesResponse>, t: Throwable) {
                        Log.e(MY_TAG, "BooksRepository.getGenreList: FAILURE!")
                        continuation.resume(Result.failure(t))
                    }

                }).apply {
                    continuation.invokeOnCancellation {
                        NonCancellable.cancel()
                    }
                }
            } catch (e: Exception){
                Log.e(MY_TAG, "BooksService.getGenreList-Exception: ${e.message}")
            }
        }
    }
}