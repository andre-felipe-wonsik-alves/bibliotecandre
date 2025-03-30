package com.example.bibliotecandre.domain.model

data class OpenLibraryResponse(
    val numFound: Int,
    val docs: List<OpenLibraryBook>
)

data class OpenLibraryBook(
    val title: String?,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val publisher: List<String>?,
    val cover_i: Int?,
    val number_of_pages: Int?
)
