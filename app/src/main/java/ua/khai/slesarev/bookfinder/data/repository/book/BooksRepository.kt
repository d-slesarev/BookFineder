package ua.khai.slesarev.bookfinder.data.repository.book

import android.util.Log
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

/* TODO: Подумай как реализовать наблюдение за данными тоблицы Books для мгновенной перерисовки домашнего списка
*   и как это сделать эффективно с точки зрения скорости подгрузки*/

class BooksRepository {

    val shelfTitlesList: List<String>
    val configShelvesList: List<ConfigShelves>
    val result: Pair<List<String>, List<ConfigShelves>>

    init {
        result = getHomeGroupContent()
        shelfTitlesList = result.first
        Log.e(MY_TAG, "shelfTitlesList: $shelfTitlesList")
        configShelvesList = result.second
    }
    private fun getHomeGroupContent(): Pair<List<String>, List<ConfigShelves>> {
        Log.i(MY_TAG, "getHomeGroupContent(): Started!")
        return try {
            ConfigShelvesBuilder
                .addRecentlyViewedShelf()
                .addRomanceShelf()
                .addFantasyShelf()
                .addAdventuresShelf()
                .addSelfDevelopmentShelf()
                .addThrillersShelf()
                .addMysteryShelf()
                .addFictionShelf()
                .build()
        } catch (e:Exception){
            Log.e(MY_TAG, "getHomeGroupContent()-Exception: ${e.message.toString()}")
            throw Exception("Что то не так с ConfigShelvesBuilder процессом!")
        }
    }
}