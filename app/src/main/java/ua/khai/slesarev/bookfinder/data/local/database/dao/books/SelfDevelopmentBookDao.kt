package ua.khai.slesarev.bookfinder.data.local.database.dao.books

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.books.SelfDevelopmentBook

@Dao
interface SelfDevelopmentBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<SelfDevelopmentBook>)

    @Query("SELECT * FROM SELF_DEVELOPMENT_BOOKS")
    fun getAllBooks(): PagingSource<Int, SelfDevelopmentBook>

    @Query("DELETE FROM SELF_DEVELOPMENT_BOOKS")
    suspend fun clearAllBooks()
}