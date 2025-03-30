package com.example.bibliotecandre.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("api/books")
    fun searchBookByISBNOpenLibrary(
        @Query("bibkeys") isbn: String,
        @Query("format") format: String = "json",
        @Query("jscmd") jscmd: String = "data"
    ): Call<Map<String, Any>>
}
