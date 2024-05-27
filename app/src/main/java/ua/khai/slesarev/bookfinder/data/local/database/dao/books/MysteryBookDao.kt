package ua.khai.slesarev.bookfinder.data.local.database.dao.books

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.books.MysteryBook
@Dao
interface MysteryBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<MysteryBook>)

    @Query("SELECT * FROM MYSTERY_BOOKS")
    fun getAllBooks(): PagingSource<Int, MysteryBook>

    @Query("DELETE FROM MYSTERY_BOOKS")
    suspend fun clearAllBooks()
}