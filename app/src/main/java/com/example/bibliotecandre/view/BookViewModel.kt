package com.example.bibliotecandre.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bibliotecandre.data.repository.BookRepository
import com.example.bibliotecandre.domain.model.VolumeInfo

class BookViewModel: ViewModel() {
    private val bookRepo = BookRepository()

    private val _book = MutableLiveData<VolumeInfo?>() // observable
    val book: LiveData<VolumeInfo?> = _book

    fun searchBook(isbn: String){
        bookRepo.getBookByISBN(isbn){
            bookInfo -> _book.postValue(bookInfo)
        }
    }
}