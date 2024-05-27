package ua.khai.slesarev.bookfinder.data.local.database.dao.books

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.books.FictionBook
@Dao
interface FictionBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<FictionBook>)

    @Query("SELECT * FROM FICTION_BOOKS")
    fun getAllBooks(): PagingSource<Int, FictionBook>

    @Query("DELETE FROM FICTION_BOOKS")
    suspend fun clearAllBooks()
}