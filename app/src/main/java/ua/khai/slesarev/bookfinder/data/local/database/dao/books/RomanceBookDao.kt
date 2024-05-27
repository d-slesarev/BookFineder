package ua.khai.slesarev.bookfinder.data.local.database.dao.books

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.books.RomanceBook

@Dao
interface RomanceBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<RomanceBook>)

    @Query("SELECT * FROM ROMANCE_BOOKS")
    fun getAllBooks(): PagingSource<Int, RomanceBook>

    @Query("DELETE FROM ROMANCE_BOOKS")
    suspend fun clearAllBooks()
}