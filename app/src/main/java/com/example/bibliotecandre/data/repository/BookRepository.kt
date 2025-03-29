package com.example.bibliotecandre.data.repository

import android.util.Log
import com.example.bibliotecandre.data.local.BookDao
import com.example.bibliotecandre.data.local.BookEntity
import com.example.bibliotecandre.data.remote.RetrofitClient
import com.example.bibliotecandre.domain.model.BookResponse
import com.example.bibliotecandre.domain.model.VolumeInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val bookDao: BookDao
) {
    suspend fun saveBook(book: BookEntity){
        bookDao.insertBook(book)
    }

    suspend fun getBooks(): List<BookEntity> {
        return bookDao.getAllBooks()
    }

    suspend fun deleteBook(book: BookEntity){
        bookDao.deleteBook(book)
    }

    suspend fun updateBookRating(bookId: Int, newRating: Int) {
        bookDao.updateRating(bookId, newRating)
    }

    fun getBookById(bookId: Int): Flow<BookEntity?> {
        return bookDao.getBookById(bookId);
    }

    fun getBookByISBN(isbn: String, callback: (List<VolumeInfo>) -> Unit){ // unit == void type
        val res = RetrofitClient.api.searchBookByISBN(isbn)

        res.enqueue(object: Callback<BookResponse>{
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {
                if(response.isSuccessful){
                    Log.d("REQ_ISBN", response.body()?.toString() ?: "No body")
                    val booksInfo = response.body()?.items?.map { it.volumeInfo } ?: emptyList()
                    callback(booksInfo)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable){
                callback(emptyList())
            }
        })
    }

    fun getBooksByTitle(title: String, callback: (List<VolumeInfo>) -> Unit) {
        val res = RetrofitClient.api.searchBookByTitle(title)

        res.enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    Log.d("REQ_TITLE", response.body()?.toString() ?: "No body")
                    val booksInfo = response.body()?.items?.map { it.volumeInfo } ?: emptyList()
                    callback(booksInfo)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                callback(emptyList())
            }
        })
    }



}