package ua.khai.slesarev.bookfinder.data.models.local.books

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "ADVENTURES_BOOKS")
data class AdventuresBook (
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "authors") val authors: String,
    @ColumnInfo(name = "thumbnail") val coverUrl: String
)
