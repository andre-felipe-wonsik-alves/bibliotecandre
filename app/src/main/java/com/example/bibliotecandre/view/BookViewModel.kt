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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(private val bookRepo: BookRepository) : ViewModel() {
    private val _book = MutableStateFlow<VolumeInfo?>(null) // observable
    val book: StateFlow<VolumeInfo?> = _book
    private val _books = MutableStateFlow<List<VolumeInfo>>(emptyList())
    val books: StateFlow<List<VolumeInfo>>  = _books
    val savedBooks = MutableStateFlow<List<BookEntity>>(emptyList())

    fun saveBook(book: BookEntity) {
        viewModelScope.launch {
            bookRepo.saveBook(book)
            getAllBooks()
        }
    }

    fun getBookById(id: Int): Flow<BookEntity?> {
        return bookRepo.getBookById(id)
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            bookRepo.deleteBook(book)
        }
    }

    fun searchBook(isbn: String) {
        viewModelScope.launch {
            val combinedBooks = mutableListOf<VolumeInfo>()

            bookRepo.getBooksOpenLibraryApi(isbn) { openLibraryBooks ->
                if (openLibraryBooks.isNotEmpty()) {
                    combinedBooks.add(openLibraryBooks.first())
                }

                bookRepo.getBooksGoogleApi(isbn) { googleBooks ->
                    combinedBooks.addAll(googleBooks)
                    _books.value = combinedBooks
                }
            }
        }
    }

    fun searchBooksByTitle(title: String) {
        bookRepo.getBooksByTitle(title) { booksInfo ->
            _books.value = booksInfo
        }
    }


    fun getAllBooks() {
        viewModelScope.launch {
            savedBooks.value = bookRepo.getBooks()
        }
    }

    fun updateBookRating(bookId: Int, newRating: Int) {
        viewModelScope.launch {
            bookRepo.updateBookRating(bookId, newRating)
        }
    }
}