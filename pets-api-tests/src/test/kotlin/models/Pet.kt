package models

data class Pet(
    val id: Int,

    val name: String,

    val category: Category,

    val photoUrls: List<String>,

    val tags: List<Tag>,

    val status: String
)
