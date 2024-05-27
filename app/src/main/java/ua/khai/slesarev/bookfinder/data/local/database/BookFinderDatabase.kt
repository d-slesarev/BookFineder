package ua.khai.slesarev.bookfinder.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.user.UserDao
import ua.khai.slesarev.bookfinder.data.local.database.dao.books.AdventuresBookDao
import ua.khai.slesarev.bookfinder.data.local.database.dao.books.FantasyBookDao
import ua.khai.slesarev.bookfinder.data.local.database.dao.books.FictionBookDao
import ua.khai.slesarev.bookfinder.data.local.database.dao.books.MysteryBookDao
import ua.khai.slesarev.bookfinder.data.local.database.dao.books.RecentBookDao
import ua.khai.slesarev.bookfinder.data.local.database.dao.books.RomanceBookDao
import ua.khai.slesarev.bookfinder.data.local.database.dao.books.SelfDevelopmentBookDao
import ua.khai.slesarev.bookfinder.data.local.database.dao.books.ThrillersBookDao
import ua.khai.slesarev.bookfinder.data.models.local.User
import ua.khai.slesarev.bookfinder.data.models.local.books.AdventuresBook
import ua.khai.slesarev.bookfinder.data.models.local.books.FantasyBook
import ua.khai.slesarev.bookfinder.data.models.local.books.FictionBook
import ua.khai.slesarev.bookfinder.data.models.local.books.MysteryBook
import ua.khai.slesarev.bookfinder.data.models.local.books.RecentBook
import ua.khai.slesarev.bookfinder.data.models.local.books.RomanceBook
import ua.khai.slesarev.bookfinder.data.models.local.books.SelfDevelopmentBook
import ua.khai.slesarev.bookfinder.data.models.local.books.ThrillersBook
import ua.khai.slesarev.bookfinder.data.util.DATABASE_NAME


@Database(
    entities = [
        User::class,
        RecentBook::class,
        AdventuresBook::class,
        ThrillersBook::class,
        MysteryBook::class,
        FantasyBook::class,
        RomanceBook::class,
        SelfDevelopmentBook::class,
        FictionBook::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BookFinderDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun RecentBookDao(): RecentBookDao
    abstract fun AdventuresBookDao(): AdventuresBookDao
    abstract fun ThrillersBookDao(): ThrillersBookDao
    abstract fun MysteryBookDao(): MysteryBookDao
    abstract fun FantasyBookDao(): FantasyBookDao
    abstract fun RomanceBookDao(): RomanceBookDao
    abstract fun SelfDevelopmentBookDao(): SelfDevelopmentBookDao
    abstract fun FictionBookDao(): FictionBookDao

    companion object {
        @Volatile
        private var instance: BookFinderDatabase? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = buildDatabase(context)
                    }
                }
            }
        }

        fun getInstance(): BookFinderDatabase {
            return instance ?: throw IllegalStateException("BookFinderDatabase is not initialized, call init(context) first")
        }

        private fun buildDatabase(context: Context): BookFinderDatabase {
            return Room.databaseBuilder(context, BookFinderDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}