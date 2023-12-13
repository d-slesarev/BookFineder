package ua.khai.slesarev.bookfinder.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid:Int,
    @ColumnInfo(name = "user_name")
    val username: String,
    @ColumnInfo(name = "email")
    val email: String
)
