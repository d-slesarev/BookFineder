package ua.khai.slesarev.bookfinder.data.repository.book

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.khai.slesarev.bookfinder.data.util.BooksResponse
import ua.khai.slesarev.bookfinder.data.model.BookItem
import ua.khai.slesarev.bookfinder.data.model.BookShelvesResponse
import ua.khai.slesarev.bookfinder.data.remote.api.google_books.GoogleBooksAPI
import ua.khai.slesarev.bookfinder.data.remote.api.google_books.RetrofitClient
import kotlin.coroutines.resume
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class BooksRepository(private val context: Context) {

    val api: GoogleBooksAPI = RetrofitClient.instance
    val authorizationToken = "Bearer ${GoogleSignIn.getLastSignedInAccount(context)?.idToken}"
    val shelfId = 1
    val apiKey = "AIzaSyAnaVw7WeDc4_keRzsSoEJkD7hE616BbWQ"

    suspend fun getBooksShelves(shelfId:Int) : BooksResponse<List<BookItem>> {
        return suspendCancellableCoroutine { continuation ->
            try {
                api.getBooksShelves(authorizationToken, shelfId, apiKey).enqueue(object :
                    Callback<BookShelvesResponse> {
                    override fun onResponse(call: Call<BookShelvesResponse>, response: Response<BookShelvesResponse>) {
                        if (response.isSuccessful) {
                            Log.d(MY_TAG, "BooksRepository.getBooksShelves: SUCCESS!")
                            // Обработка успешного ответа
                            val bookShelvesResponse = response.body()
                            if (bookShelvesResponse != null) {
                                continuation.resume(BooksResponse.Success(bookShelvesResponse.items))
                            }
                        } else {
                            continuation.resume(BooksResponse.Error("Unknown Error"))
                        }
                    }

                    override fun onFailure(call: Call<BookShelvesResponse>, t: Throwable) {
                        Log.d(MY_TAG, "BooksRepository.getBooksShelves: FAILURE!")
                        continuation.resume(BooksResponse.Error("Unknown Error"))
                    }
                })
            } catch (e: Exception){
                Log.d(MY_TAG, "FirebaseAuthServ.sendEmailVer-Exception: ${e.message}")
            }
        }
    }
}