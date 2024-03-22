package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ua.khai.slesarev.bookfinder.data.model.BookItem
import ua.khai.slesarev.bookfinder.data.repository.book.BooksRepository
import ua.khai.slesarev.bookfinder.ui.util.StateHomeList
import ua.khai.slesarev.bookfinder.ui.util.UiState

class HomeScreenViewModel(private val application: Application) : AndroidViewModel(application)  {

    val uiState: MutableLiveData<StateHomeList<Map<String, List<BookItem>>>> = MutableLiveData()
    private val bookRepository = BooksRepository(application)
    suspend fun loadContent(){
        val map = bookRepository.getHomeGroupContent()
        if (map.isNotEmpty()){
            uiState.value = StateHomeList.Success(map)
        }

    }

}