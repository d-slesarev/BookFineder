package ua.khai.slesarev.bookfinder.ui.util

sealed class UiState {
    object Loading : UiState()
    data class Success(val response: String) : UiState()
    data class Error(val response: String) : UiState()
}
