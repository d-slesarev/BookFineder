package ua.khai.slesarev.bookfinder.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.util.DATABASE_NAME


@Database(entities = [User::class], version = 3, exportSchema = false)
abstract class BookFinderDatabase : RoomDatabase(){
    abstract fun userDao(): UserDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: BookFinderDatabase? = null

        fun getInstance(context: Context): BookFinderDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): BookFinderDatabase {
            return Room.databaseBuilder(context, BookFinderDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}