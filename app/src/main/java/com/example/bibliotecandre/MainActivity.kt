package com.example.bibliotecandre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecandre.ui.theme.BibliotecandreTheme
import com.example.bibliotecandre.view.BookViewModel
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookSearchScreen()
        }
    }
}

@Composable
fun BookSearchScreen(viewModel: BookViewModel = viewModel()) {
    var isbn by remember { mutableStateOf("") }
    val book by viewModel.book.collectAsState()

    Column(modifier = Modifier.padding(32.dp)) {
        TextField(
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text("Digite o ISBN") }
        )
        Button(onClick = { viewModel.searchBook(isbn) }) {
            Text("Buscar Livro")
        }
        book?.let {
            Text("Título: ${it.title}")
            it.authors?.forEach { author -> Text("Autor: $author") }
            it.imageLinks?.thumbnail?.let { url ->
                AsyncImage(model = url, contentDescription = "Capa do Livro")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BibliotecandreTheme {
        Greeting("Android")
    }
}