package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ua.khai.slesarev.bookfinder.data.model.BookItem
import ua.khai.slesarev.bookfinder.ui.util.StateHomeList
import ua.khai.slesarev.bookfinder.ui.util.UiState

class HomeScreenViewModel(application: Application) : AndroidViewModel(application)  {

    val uiState: MutableLiveData<StateHomeList<List<BookItem>>> = MutableLiveData()

}