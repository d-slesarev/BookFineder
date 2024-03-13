package ua.khai.slesarev.bookfinder.data.util

sealed class BooksResponse<out T> {
    data class Success<out T>(val data: T) : BooksResponse<T>()
    data class Error(val errorMessage: String) : BooksResponse<Nothing>()
}
