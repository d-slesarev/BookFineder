package ua.khai.slesarev.bookfinder.ui.util

sealed class UiState {
    object Loading : UiState()
    data class Success(val response: String) : UiState()
    data class Error(val response: String) : UiState()
}

sealed class StateHomeList<out T> {
    object Loading : StateHomeList<Nothing>()
    data class Success<out T>(val response: T) : StateHomeList<T>()
    data class Error(val response: String) : StateHomeList<Nothing>()
}
