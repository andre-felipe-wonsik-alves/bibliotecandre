package com.example.bibliotecandre.data.repository

import com.example.bibliotecandre.data.local.BookDao
import com.example.bibliotecandre.data.local.BookEntity
import com.example.bibliotecandre.data.remote.RetrofitClient
import com.example.bibliotecandre.domain.model.BookResponse
import com.example.bibliotecandre.domain.model.VolumeInfo
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

    fun getBookByISBN(isbn: String, callback: (VolumeInfo?) -> Unit){ // unit == void type
        val res = RetrofitClient.api.searchBookByISBN("isbn:$isbn")

        res.enqueue(object: Callback<BookResponse>{
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {
                if(response.isSuccessful){
                    val bookInfo = response.body()?.items?.firstOrNull()?.volumeInfo
                    print("VALOR: $bookInfo")
                    callback(bookInfo)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable){
                callback(null)
            }
        })
    }
}