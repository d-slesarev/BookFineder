package ua.khai.slesarev.bookfinder.data.paging.mediators

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.models.local.books.FantasyBook
import ua.khai.slesarev.bookfinder.data.models.local.books.MysteryBook
import ua.khai.slesarev.bookfinder.data.models.remote.book.BookItem
import ua.khai.slesarev.bookfinder.data.remote.api.google_books.BooksService
import ua.khai.slesarev.bookfinder.data.repository.book.BookType
import ua.khai.slesarev.bookfinder.data.repository.book.TypeCall
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MysteryRemoteMediator (
    private val context: Context,
    private val typeCall: TypeCall,
    private val apiService: BooksService,
    private val bookDatabase: BookFinderDatabase
) : RemoteMediator<Int, MysteryBook>() {
    override suspend fun initialize(): InitializeAction {
        Log.d(MY_TAG, "MysteryRemoteMediator initialize called")
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MysteryBook>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                resetCurrentPosition(context)
                0
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem  = state.lastItemOrNull()
                if (lastItem  == null) {
                    return MediatorResult.Success(endOfPaginationReached = false)
                }
                val currentPosition = getCurrentPosition(context) + 20
                setCurrentPosition(context, currentPosition)
                currentPosition
            }
        }

        return try {
            var index: Int = if (loadType == LoadType.REFRESH) 1
            else state.lastItemOrNull()!!.id

            Log.d(MY_TAG, "API Call IN MysteryRemoteMediator: Started!\n" +
                    "StartIndex = ${page}\n" +
                    "loadType = ${loadType.name}")

            val list: List<BookItem> = apiService.performGoogleBooksRequest(
                startIndex = page,
                maxResults = state.config.pageSize,
                typeCall = typeCall
            ).getOrNull()?.filter { item ->
                item.volumeInfo?.title != null &&
                        item.volumeInfo.authors != null &&
                        item.volumeInfo.imageLinks?.thumbnail != null
            } ?: emptyList()

            val clearList = list.distinctBy {
                it.volumeInfo!!.title.toString()
            }.distinctBy {
                it.volumeInfo!!.imageLinks!!.thumbnail.toString()
            }

            val books:List<MysteryBook> = if (clearList.isNotEmpty()){
                clearList.map  {
                    it.volumeInfo?.title?.let { it1 ->
                        it.volumeInfo.authors?.let { it2 ->
                            it.volumeInfo.imageLinks?.thumbnail?.let { it3 ->
                                MysteryBook(
                                    id = index++,
                                    title = it1,
                                    authors = it2.joinToString(", "),
                                    coverUrl = it3
                                )
                            }
                        }
                    }?: throw Exception("Incomplete BookItem data!!!")
                }
            } else { emptyList() }

            val endOfPaginationReached = books.isEmpty()

            withContext(Dispatchers.IO) {
                if (loadType == LoadType.REFRESH) {
                    bookDatabase.MysteryBookDao().clearAllBooks()
                }
                bookDatabase.MysteryBookDao().insertAll(books)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            Log.e(MY_TAG, "MysteryRemoteMediator-Exception: ${e.message}")
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            Log.e(MY_TAG, "MysteryRemoteMediator-Exception: ${e.message}")
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e(MY_TAG, "MysteryRemoteMediator-Exception: ${e.message}")
            return MediatorResult.Error(e)
        }
    }

    companion object PreferencesHelper {

        private const val PREFS_NAME = "mystery_book_prefs"
        private const val KEY_CURRENT_POSITION = "mystery_current_position"

        private fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        fun getCurrentPosition(context: Context): Int {
            return getPreferences(context).getInt(KEY_CURRENT_POSITION, 0)
        }

        fun setCurrentPosition(context: Context, position: Int) {
            getPreferences(context).edit().putInt(KEY_CURRENT_POSITION, position).apply()
        }

        fun resetCurrentPosition(context: Context) {
            setCurrentPosition(context, 0)
        }
    }
}