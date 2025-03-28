package com.example.bibliotecandre.data.local

import androidx.room.*
import java.util.Date

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String?,
    val authors: String?,
    val thumbnail: String?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val pageCount: Int?,
    val rating: Int = 0,
//  COLOCAR CATEGORIAS DEPOIS
)
