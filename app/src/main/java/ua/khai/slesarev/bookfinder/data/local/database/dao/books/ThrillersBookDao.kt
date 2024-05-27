package ua.khai.slesarev.bookfinder.data.local.database.dao.books

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.books.ThrillersBook

@Dao
interface ThrillersBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<ThrillersBook>)

    @Query("SELECT * FROM THRILLER_BOOKS")
    fun getAllBooks(): PagingSource<Int, ThrillersBook>

    @Query("DELETE FROM THRILLER_BOOKS")
    suspend fun clearAllBooks()
}