package ua.khai.slesarev.bookfinder.data.models.remote.profile

data class Person(
    val names: List<Name>,
    val photos: List<Photo>,
    val emailAddresses: List<EmailAddress>
)
