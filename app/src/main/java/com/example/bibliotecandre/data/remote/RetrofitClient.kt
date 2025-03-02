package com.example.bibliotecandre.data.remote

import com.example.bibliotecandre.domain.model.GoogleBooksApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    val api: GoogleBooksApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApi::class.java)}
}