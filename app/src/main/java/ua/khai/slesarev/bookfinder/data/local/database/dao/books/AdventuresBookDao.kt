package ua.khai.slesarev.bookfinder.data.local.database.dao.books

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.books.AdventuresBook
@Dao
interface AdventuresBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<AdventuresBook>)

    @Query("SELECT * FROM ADVENTURES_BOOKS")
    fun getAllBooks(): PagingSource<Int, AdventuresBook>

    @Query("DELETE FROM ADVENTURES_BOOKS")
    suspend fun clearAllBooks()
}