package com.example.bibliotecandre

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecandre.view.BookViewModel
import coil.compose.AsyncImage
import com.example.bibliotecandre.data.local.BookEntity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bibliotecandre.ui.theme.BibliotecandreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column(modifier = Modifier.padding(top = 50.dp)) {
                BibliotecandreTheme {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "book_list") {
        composable("book_list") {
            BookListScreen(navController)
        }
        composable("add_book") {
            AddBookScreen(navController)
        }
    }
}


@Composable
fun BookSearchScreen(viewModel: BookViewModel = hiltViewModel()) {
    var isbn by remember { mutableStateOf("") }
    val book by viewModel.book.collectAsState()

    Column {
        // Linha com barra de pesquisa e botão de busca
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = isbn,
                onValueChange = { isbn = it },
                label = { Text("Digite o ISBN") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { viewModel.searchBook(isbn) }) {
                Text("Buscar Livro")
            }
        }

        book?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        book?.let {
                            val bookEntity = BookEntity(
                                title = it.title,
                                authors = it.authors?.joinToString(", "),
                                thumbnail = it.imageLinks?.thumbnail
                            )
                            viewModel.saveBook(bookEntity)
                            viewModel.getAllBooks()
                        }
                    }
                ) {
                    Text("Salvar no Banco")
                }
            }
        }
    }
}

@Composable
fun BookListScreen(navController: NavController, viewModel: BookViewModel = hiltViewModel()) {
    val books by viewModel.books.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Bibliotecandre", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { navController.navigate("add_book") }) {
                Text("Adicionar Livro")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        viewModel.getAllBooks()

        if (books.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Está vazio por aqui...", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Exibir 2 colunas
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(books) { book ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            book.thumbnail?.let { url ->
                                AsyncImage(model = url, contentDescription = "Capa do Livro")
                            }
                            Text("Título: ${book.title}")
                            Text("Autor: ${book.authors}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddBookScreen(navController: NavController, viewModel: BookViewModel = hiltViewModel()) {
    var isbn by remember { mutableStateOf("") }
    val book by viewModel.book.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Buscar Livro", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text("Digite o ISBN") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.searchBook(isbn) }) {
            Text("Buscar Livro")
        }

        Spacer(modifier = Modifier.height(16.dp))

        book?.let {
            Text("Título: ${it.title}")
            it.authors?.forEach { author -> Text("Autor: $author") }
            it.imageLinks?.thumbnail?.let { url ->
                AsyncImage(model = url, contentDescription = "Capa do Livro")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val bookEntity = BookEntity(
                    title = it.title,
                    authors = it.authors?.joinToString(", "),
                    thumbnail = it.imageLinks?.thumbnail
                )
                viewModel.saveBook(bookEntity)
                Toast.makeText(context, "Livro salvo com sucesso!", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Fecha a tela e volta para a lista
            }) {
                Text("Salvar Livro")
            }
        }
    }
}