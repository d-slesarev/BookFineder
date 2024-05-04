package ua.khai.slesarev.bookfinder.data.repository.book

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.khai.slesarev.bookfinder.data.util.BooksResponse
import ua.khai.slesarev.bookfinder.data.model.BookItem
import ua.khai.slesarev.bookfinder.data.model.BookShelvesResponse
import ua.khai.slesarev.bookfinder.data.remote.api.google_books.GoogleBooksAPI
import ua.khai.slesarev.bookfinder.data.remote.api.google_books.RetrofitClientGB
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.TokenStorage
import kotlin.coroutines.resume
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.data.util.getGroupTitles

/* TODO: Подумай как реализовать наблюдение за данными тоблицы Books для мгновенной перерисовки домашнего списка
*   и как это сделать эффективно с точки зрения скорости подгрузки*/

class BooksRepository(private val context: Context) {

    private val api: GoogleBooksAPI = RetrofitClientGB.instance
    private val authorizationToken = "Bearer ${TokenStorage.accessToken}"
    private val apiKey = "AIzaSyAnaVw7WeDc4_keRzsSoEJkD7hE616BbWQ"
    private val homeGroupTitles: List<Pair<String, Any>> = getGroupTitles()
    private var auth: FirebaseAuth = Firebase.auth

    private suspend fun getBooksShelves() : BooksResponse<List<BookItem>> {
        return suspendCancellableCoroutine { continuation ->
            try {
                Log.i(MY_TAG, "authorizationToken: $authorizationToken")
                api.getBooksShelves(authorizationToken, apiKey = apiKey).enqueue(object :
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
                            Log.d(MY_TAG, "BooksRepository.getBooksShelves: FAILURE!")
                            continuation.resume(BooksResponse.Error("Unknown Error"))
                        }
                    }

                    override fun onFailure(call: Call<BookShelvesResponse>, t: Throwable) {
                        Log.d(MY_TAG, "BooksRepository.getBooksShelves: FAILURE!")
                        continuation.resume(BooksResponse.Error("Unknown Error"))
                    }
                }).apply {
                    // Регистрируем обработчик отмены для асинхронной операции
                    continuation.invokeOnCancellation {
                        cancel() // Отменяем операцию, если корутина отменена
                    }
                }
            } catch (e: Exception){
                Log.d(MY_TAG, "FirebaseAuthServ.sendEmailVer-Exception: ${e.message}")
            }
        }
    }
    private suspend fun getNewestEbook() : BooksResponse<List<BookItem>> {
        return suspendCancellableCoroutine { continuation ->
            try {
                api.getNewestEbook(apiKey).enqueue(object :
                    Callback<BookShelvesResponse> {
                    override fun onResponse(call: Call<BookShelvesResponse>, response: Response<BookShelvesResponse>) {
                        if (response.isSuccessful) {
                            Log.d(MY_TAG, "BooksRepository.getNewestEbook: SUCCESS!")
                            // Обработка успешного ответа
                            val bookShelvesResponse = response.body()
                            if (bookShelvesResponse != null) {
                                continuation.resume(BooksResponse.Success(bookShelvesResponse.items))
                            }
                        } else {
                            Log.d(MY_TAG, "BooksRepository.getNewestEbook: FAILURE!")
                            continuation.resume(BooksResponse.Error("Unknown Error"))
                        }
                    }

                    override fun onFailure(call: Call<BookShelvesResponse>, t: Throwable) {
                        Log.d(MY_TAG, "BooksRepository.getNewestEbook: FAILURE!")
                        continuation.resume(BooksResponse.Error("Unknown Error"))
                    }
                }).apply {
                    // Регистрируем обработчик отмены для асинхронной операции
                    continuation.invokeOnCancellation {
                        cancel() // Отменяем операцию, если корутина отменена
                    }
                }
            } catch (e: Exception){
                Log.d(MY_TAG, "FirebaseAuthServ.sendEmailVer-Exception: ${e.message}")
            }
        }
    }
    private suspend fun getGenreList(genreQuery: String) : BooksResponse<List<BookItem>> {
        return suspendCancellableCoroutine { continuation ->
            try {
                api.getGenreList(genreQuery, apiKey).enqueue(object :
                    Callback<BookShelvesResponse> {
                    override fun onResponse(call: Call<BookShelvesResponse>, response: Response<BookShelvesResponse>) {
                        if (response.isSuccessful) {
                            Log.d(MY_TAG, "BooksRepository.getGenreList: SUCCESS!")
                            // Обработка успешного ответа
                            val bookShelvesResponse = response.body()
                            if (bookShelvesResponse != null) {
                                continuation.resume(BooksResponse.Success(bookShelvesResponse.items))
                            }
                        } else {
                            Log.d(MY_TAG, "BooksRepository.getGenreList: FAILURE!")
                            continuation.resume(BooksResponse.Error("Unknown Error"))
                        }
                    }

                    override fun onFailure(call: Call<BookShelvesResponse>, t: Throwable) {
                        Log.d(MY_TAG, "BooksRepository.getGenreList: FAILURE!")
                        continuation.resume(BooksResponse.Error("Unknown Error"))
                    }
                }).apply {
                    // Регистрируем обработчик отмены для асинхронной операции
                    continuation.invokeOnCancellation {
                        cancel() // Отменяем операцию, если корутина отменена
                    }
                }
            } catch (e: Exception){
                Log.e(MY_TAG, "BooksRepository.getGenreList-Exception: ${e.message}")
            }
        }
    }
    private suspend fun getAccessToken(){

        auth.getAccessToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                Log.e(MY_TAG, "AccessToken: $idToken")
            } else {
                Log.e(MY_TAG, "AccessToken: Error!")
            }
        }
    }
    suspend fun getHomeGroupContent() : Map<String, List<BookItem>>{
        val groupsContent = mutableMapOf<String, List<BookItem>>()
        val resultBS: BooksResponse<List<BookItem>>
        val resultNE: BooksResponse<List<BookItem>>
        var resultGL: BooksResponse<List<BookItem>>
        var pair: Pair<String,String>

        try {
            Log.i(MY_TAG, "Before: getBooksShelves()")
            resultBS = getBooksShelves()

            if(resultBS is BooksResponse.Success){
                Log.e(MY_TAG, "Before: put(resultBS)")
                groupsContent.put(homeGroupTitles[0].second.toString(), resultBS.data)
            }

            Log.i(MY_TAG, "Before: getNewestEbook()")
            resultNE = getNewestEbook()
            if (resultNE is BooksResponse.Success){
                Log.e(MY_TAG, "Before: put(resultNE)")
                groupsContent.put(homeGroupTitles[1].second.toString(), resultNE.data)
                Log.e(MY_TAG, "\ngroupsContent: ${groupsContent.values.size}\n")
            }

            for (i in 2..6) {

                if (homeGroupTitles[i].second is Pair<*, *>){
                    pair = homeGroupTitles[i].second as Pair<String, String>
                    Log.i(MY_TAG, "Before: getGenreList()")
                    resultGL = getGenreList(pair.second)

                    if (resultGL is BooksResponse.Success){
                        Log.e(MY_TAG, "Before: put(resultGL)")
                        groupsContent.put(pair.first, resultGL.data)
                    }
                }
            }

        } catch (e:Exception) {
            Log.e(MY_TAG, "BooksRepository.getHomeGroupContent-Exception: ${e.message}")
        }

        Log.e(MY_TAG, "\ngroupsContent: ${groupsContent.values.size}\n")

        getAccessToken()

        Log.i(MY_TAG, "Before: RETURN!")
        return groupsContent
    }
}