package com.example.bibliotecandre.data.local
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("SELECT * FROM books WHERE :bookId = books.id")
    fun getBookById(bookId: Int): Flow<BookEntity?>

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("UPDATE books SET rating = :newRating WHERE id = :bookId")
    suspend fun updateRating(bookId: Int, newRating: Int)

}