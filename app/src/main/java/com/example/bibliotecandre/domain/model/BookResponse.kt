package com.example.bibliotecandre.domain.model

import com.google.gson.annotations.SerializedName

data class BookResponse(
    val items: List<BookItem>?
)

data class BookItem(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
    val title: String?,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val pageCount: Int?,
    val imageLinks: ImageLinks?,

)

data class ImageLinks(
    @SerializedName("smallThumbnail") val smallThumbnail: String?,
    @SerializedName("thumbnail") val thumbnail: String?
)

