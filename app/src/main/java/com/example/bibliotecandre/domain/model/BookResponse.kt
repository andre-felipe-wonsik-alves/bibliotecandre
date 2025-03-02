package com.example.bibliotecandre.domain.model

data class BookResponse(
    val items: List<BookItem>? // lista de varios itens da requisicao
)

data class BookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val publishedDate: String?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String? //link para a foto da capa
)