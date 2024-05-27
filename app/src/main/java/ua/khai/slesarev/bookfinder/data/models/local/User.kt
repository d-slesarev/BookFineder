package ua.khai.slesarev.bookfinder.data.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "USERS")
data class User(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "user_name") var username: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "image_uri") var imageUri: String
)
