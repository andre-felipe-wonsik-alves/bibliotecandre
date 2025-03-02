package com.example.bibliotecandre.data.repository

import com.example.bibliotecandre.data.remote.RetrofitClient
import com.example.bibliotecandre.domain.model.BookResponse
import com.example.bibliotecandre.domain.model.VolumeInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookRepository {
    fun getBookByISBN(isbn: String, callback: (VolumeInfo?) -> Unit){ // unit == void type
        val res = RetrofitClient.api.searchBookByISBN("isbn:$isbn")

        res.enqueue(object: Callback<BookResponse>{
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {
                if(response.isSuccessful){
                    val bookInfo = response.body()?.items?.firstOrNull()?.volumeInfo
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