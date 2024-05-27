package ua.khai.slesarev.bookfinder.data.repository.book

import android.content.Context
import ua.khai.slesarev.bookfinder.BookFinderApp
import ua.khai.slesarev.bookfinder.R.string
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
enum class BookType {
    RECENT,
    ROMANCE,
    FANTASY,
    ADVENTURES,
    SELF_DEVELOPMENT,
    THRILLERS,
    MYSTERY,
    FICTION
}

val muApp: BookFinderApp = BookFinderApp.instance
val context: Context = muApp.applicationContext

val genreQueryMap = mapOf(
    context.getString(string.adventures) to context.getString(string.adventures_tags),
    context.getString(string.thrillers) to context.getString(string.thrillers_tags),
    context.getString(string.mystery) to context.getString(string.mystery_tags),
    context.getString(string.fantasy) to context.getString(string.fantasy_tags),
    context.getString(string.romance) to context.getString(string.romance_tags),
    context.getString(string.self_development) to context.getString(string.self_development_tags),
    context.getString(string.fiction) to context.getString(string.fiction_tags)
)

val database = BookFinderDatabase.getInstance()
data class ConfigShelves(
    val shelfName: String,
    val typeCallAPI: TypeCall,
    val type: BookType
)
interface TypeCall
data class RecentlyViewed(
    val shelfId: Int
) : TypeCall
data class GenreList(
    val genreQuery: String?
) : TypeCall

object ConfigShelvesBuilder {
    private val shelfTitlesList: MutableList<String> = mutableListOf()
    private val configShelvesList: MutableList<ConfigShelves> = mutableListOf()
    fun addRecentlyViewedShelf(): ConfigShelvesBuilder {
        val titleShelf:String = context.getString(string.recently_viewed)
        shelfTitlesList.add(titleShelf)
        configShelvesList.add(
            ConfigShelves(
                shelfName = titleShelf,
                typeCallAPI = RecentlyViewed(shelfId = 7),
                type = BookType.RECENT
            )
        )
        return this
    }
    fun addAdventuresShelf(): ConfigShelvesBuilder {
        val titleShelf:String = context.getString(string.adventures)
        shelfTitlesList.add(titleShelf)
        configShelvesList.add(
            ConfigShelves(
                shelfName = titleShelf,
                typeCallAPI = GenreList(genreQuery = genreQueryMap[titleShelf]),
                type = BookType.ADVENTURES
            )
        )
        return this
    }
    fun addThrillersShelf(): ConfigShelvesBuilder {
        val titleShelf:String = context.getString(string.thrillers)
        shelfTitlesList.add(titleShelf)
        configShelvesList.add(
            ConfigShelves(
                shelfName = titleShelf,
                typeCallAPI = GenreList(genreQuery = genreQueryMap[titleShelf]),
                type = BookType.THRILLERS
            )
        )
        return this
    }
    fun addMysteryShelf(): ConfigShelvesBuilder {
        val titleShelf:String = context.getString(string.mystery)
        shelfTitlesList.add(titleShelf)
        configShelvesList.add(
            ConfigShelves(
                shelfName = titleShelf,
                typeCallAPI = GenreList(genreQuery = genreQueryMap[titleShelf]),
                type = BookType.MYSTERY
            )
        )
        return this
    }
    fun addFantasyShelf(): ConfigShelvesBuilder {
        val titleShelf:String = context.getString(string.fantasy)
        shelfTitlesList.add(titleShelf)
        configShelvesList.add(
            ConfigShelves(
                shelfName = titleShelf,
                typeCallAPI = GenreList(genreQuery = genreQueryMap[titleShelf]),
                type = BookType.FANTASY
            )
        )
        return this
    }
    fun addRomanceShelf(): ConfigShelvesBuilder {
        val titleShelf:String = context.getString(string.romance)
        shelfTitlesList.add(titleShelf)
        configShelvesList.add(
            ConfigShelves(
                shelfName = titleShelf,
                typeCallAPI = GenreList(genreQuery = genreQueryMap[titleShelf]),
                type = BookType.ROMANCE
            )
        )
        return this
    }
    fun addSelfDevelopmentShelf(): ConfigShelvesBuilder {
        val titleShelf:String = context.getString(string.self_development)
        shelfTitlesList.add(titleShelf)
        configShelvesList.add(
            ConfigShelves(
                shelfName = titleShelf,
                typeCallAPI = GenreList(genreQuery = genreQueryMap[titleShelf]),
                type = BookType.SELF_DEVELOPMENT
            )
        )
        return this
    }
    fun addFictionShelf(): ConfigShelvesBuilder {
        val titleShelf:String = context.getString(string.fiction)
        shelfTitlesList.add(titleShelf)
        configShelvesList.add(
            ConfigShelves(
                shelfName = titleShelf,
                typeCallAPI = GenreList(genreQuery = genreQueryMap[titleShelf]),
                type = BookType.FICTION
            )
        )
        return this
    }
    fun build(): Pair<List<String>, List<ConfigShelves>> {
        return Pair(shelfTitlesList, configShelvesList)
    }
}



























