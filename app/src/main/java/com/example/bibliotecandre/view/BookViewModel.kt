package com.example.bibliotecandre.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecandre.data.local.BookEntity
import com.example.bibliotecandre.data.repository.BookRepository
import com.example.bibliotecandre.domain.model.VolumeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(private val bookRepo: BookRepository) : ViewModel() {
    private val _book = MutableStateFlow<VolumeInfo?>(null) // observable
    val book: StateFlow<VolumeInfo?> = _book

    fun saveBook(book: BookEntity) {
        viewModelScope.launch {
            bookRepo.saveBook(book)
            getAllBooks()
        }
    }

    fun getBooks(callback: (List<BookEntity>) -> Unit) {
        viewModelScope.launch {
            val books = bookRepo.getBooks()
            callback(books)
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            bookRepo.deleteBook(book)
        }
    }

    fun searchBook(isbn: String) {
        bookRepo.getBookByISBN(isbn) { bookInfo ->
            _book.value = bookInfo
        }
    }

    val books = MutableStateFlow<List<BookEntity>>(emptyList())

    fun getAllBooks() {
        viewModelScope.launch {
            books.value = bookRepo.getBooks()
        }
    }
}