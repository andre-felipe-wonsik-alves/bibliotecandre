package com.example.bibliotecandre.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun bookDao(): BookDao
}