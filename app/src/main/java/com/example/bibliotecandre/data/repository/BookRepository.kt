package com.example.bibliotecandre.data.repository

import android.util.Log
import com.example.bibliotecandre.data.local.BookDao
import com.example.bibliotecandre.data.local.BookEntity
import com.example.bibliotecandre.data.remote.RetrofitClient
import com.example.bibliotecandre.domain.model.BookResponse
import com.example.bibliotecandre.domain.model.ImageLinks
import com.example.bibliotecandre.domain.model.OpenLibraryResponse
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

    fun getBooksGoogleApi(isbn: String, callback: (List<VolumeInfo>) -> Unit){ // unit == void type
        val res = RetrofitClient.api.searchBookByISBN(isbn)

        res.enqueue(object: Callback<BookResponse>{
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {
                if(response.isSuccessful){
                    Log.d("REQ_GOOGLE_API", response.body()?.toString() ?: "No body")
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

    fun getBooksOpenLibraryApi(isbn: String, callback: (List<VolumeInfo>) -> Unit) {
        val res = RetrofitClient.openLibraryApi.searchBookByISBNOpenLibrary("ISBN:$isbn")

        res.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    val bookInfo = response.body()?.get("ISBN:$isbn") as? Map<String, Any>
                    val volumeInfo = bookInfo?.let {
                        VolumeInfo(
                            title = it["title"] as? String,
                            authors = (it["authors"] as? List<Map<String, String>>)
                                ?.mapNotNull { author -> author["name"] }, // Correção aqui
                            publisher = (it["publishers"] as? List<Map<String, String>>)
                                ?.mapNotNull { publisher -> publisher["name"] }
                                ?.joinToString(", "),
                            publishedDate = it["publish_date"] as? String,
                            description = (it["description"] as? Map<String, String>)?.get("value") ?: it["description"] as? String,
                            pageCount = it["number_of_pages"] as? Int,
                            imageLinks = (it["cover"] as? Map<String, Any>)?.let { cover ->
                                val coverId = cover["id"] as? Int
                                coverId?.let { id ->
                                    ImageLinks(
                                        smallThumbnail = "https://covers.openlibrary.org/b/id/$id-S.jpg",
                                        thumbnail = "https://covers.openlibrary.org/b/id/$id-L.jpg"
                                    )
                                }
                            }
                        )
                    }
                    Log.d("REQ_OPEN_BOOKS_API", volumeInfo.toString())
                    callback(volumeInfo?.let { listOf(it) } ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
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