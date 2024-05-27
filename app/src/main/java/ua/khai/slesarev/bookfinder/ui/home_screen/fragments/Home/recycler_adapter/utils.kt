package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ua.khai.slesarev.bookfinder.data.models.local.books.Book
import ua.khai.slesarev.bookfinder.data.repository.book.BookType

data class BookTypeName(
    val title: String,
    val type: BookType
)