package com.example.bibliotecandre.data.local

import androidx.room.*

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String?,
    val authors: String?,
    val thumbnail: String?,
)
