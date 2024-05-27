package ua.khai.slesarev.bookfinder.data.models.local.books

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "FICTION_BOOKS")
data class FictionBook (
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "authors") val authors: String,
    @ColumnInfo(name = "thumbnail") val coverUrl: String
)
