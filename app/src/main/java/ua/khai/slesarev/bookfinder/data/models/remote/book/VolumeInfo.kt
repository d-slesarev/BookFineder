package ua.khai.slesarev.bookfinder.data.models.remote.book

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val imageLinks: ImageLinks?
)
