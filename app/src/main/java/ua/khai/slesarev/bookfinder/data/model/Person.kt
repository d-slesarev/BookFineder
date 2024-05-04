package ua.khai.slesarev.bookfinder.data.model

data class Person(
    val names: List<Name>,
    val photos: List<Photo>,
    val emailAddresses: List<EmailAddress>
)
