package com.example.bibliotecandre.data.remote

import com.example.bibliotecandre.domain.model.BookResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface GoogleBooksApi {
    @GET("volumes")
    fun searchBookByISBN(
        @Query("q") query: String // query usada
    ): Call<BookResponse> // validacao

    @GET("volumes")
    fun searchBookByTitle(
        @Query("q") query: String
    ): Call<BookResponse>
}