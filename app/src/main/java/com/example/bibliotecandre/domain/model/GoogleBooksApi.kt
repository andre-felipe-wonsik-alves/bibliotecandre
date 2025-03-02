package com.example.bibliotecandre.domain.model

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface GoogleBooksApi {
    @GET("volumes")
    fun searchBookByISBN(
        @Query("q") query: String // query usada
    ): Call<BookResponse> // validacao
}