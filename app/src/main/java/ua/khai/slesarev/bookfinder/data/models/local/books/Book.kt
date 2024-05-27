package ua.khai.slesarev.bookfinder.data.models.local.books
interface Book {
    val id: Int
    val title: String
    val authors: String
    val coverUrl: String

    fun equals(other: Book?): Boolean
}