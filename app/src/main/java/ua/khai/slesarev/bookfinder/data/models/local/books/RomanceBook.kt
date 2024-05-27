package ua.khai.slesarev.bookfinder.data.models.local.books

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "ROMANCE_BOOKS")
data class RomanceBook (
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "authors") val authors: String,
    @ColumnInfo(name = "thumbnail") val coverUrl: String
)
