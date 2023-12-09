package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp

import ua.khai.slesarev.bookfinder.util.AccountHelper.Response

sealed class UiState {
    object Loading : UiState()
    data class Success(val response: String) : UiState()
    data class Error(val response: String) : UiState()
}
