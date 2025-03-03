package com.example.bibliotecandre.view

import androidx.lifecycle.ViewModel
import com.example.bibliotecandre.data.repository.BookRepository
import com.example.bibliotecandre.domain.model.VolumeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BookViewModel: ViewModel() {
    private val bookRepo = BookRepository()

    private val _book = MutableStateFlow<VolumeInfo?>(null) // observable
    val book: StateFlow<VolumeInfo?> = _book

    fun searchBook(isbn: String){
        bookRepo.getBookByISBN(isbn){
            bookInfo -> _book.value = bookInfo
        }
    }
}