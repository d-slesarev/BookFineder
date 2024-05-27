package ua.khai.slesarev.bookfinder.data.local.database.dao.books

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.books.RecentBook

@Dao
interface RecentBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<RecentBook>)
    @Query("SELECT * FROM RECENT_BOOKS")
    fun getAllBooks(): PagingSource<Int, RecentBook>
    @Query("DELETE FROM RECENT_BOOKS")
    suspend fun clearAllBooks()
}