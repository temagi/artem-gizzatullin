package models

data class EmptyPet(
    val id: Int? = null,

    val name: String? = null,

    val category: Category? = null,

    val photoUrls: List<String>? = null,

    val tags: List<Tag>? = null,

    val status: String? = null
)
