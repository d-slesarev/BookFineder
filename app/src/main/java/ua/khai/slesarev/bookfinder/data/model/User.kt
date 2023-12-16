package ua.khai.slesarev.bookfinder.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = false)
    var uid:String = "",
    @ColumnInfo(name = "user_name")
    var username: String,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "image_uri")
    var imageUri: String
)
