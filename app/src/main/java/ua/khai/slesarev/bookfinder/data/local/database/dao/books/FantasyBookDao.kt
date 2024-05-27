package ua.khai.slesarev.bookfinder.data.local.database.dao.books

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.books.FantasyBook
@Dao
interface FantasyBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<FantasyBook>)

    @Query("SELECT * FROM FANTASY_BOOKS")
    fun getAllBooks(): PagingSource<Int, FantasyBook>

    @Query("DELETE FROM FANTASY_BOOKS")
    suspend fun clearAllBooks()
}