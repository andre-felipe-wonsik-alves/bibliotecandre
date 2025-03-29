package com.example.bibliotecandre

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
        composable("view_book/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: -1
            ViewBookScreen(navController, bookId)
        }
    }
}

@Composable
fun ViewBookScreen(
    navController: NavController,
    bookId: Int,
    viewModel: BookViewModel = hiltViewModel()
) {
    val book by viewModel.getBookById(bookId).collectAsState(initial = null)
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0) }

    LaunchedEffect(book) {
        book?.let { rating = it.rating }
    }

    if (book == null) {
        Text("Carregando...")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, bottom = 50.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            book?.let { safeBook ->
                Text("${safeBook.title}", style = MaterialTheme.typography.headlineSmall)
                Text("${safeBook.authors}", style = MaterialTheme.typography.bodyLarge)
                Text("${safeBook.publisher}", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Data de Publicação: ${safeBook.publishedDate}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Descrição: ${safeBook.description}",
                    style = MaterialTheme.typography.bodyLarge
                )

                safeBook.thumbnail?.let {
                    val fixedThumbnailUrl = it.replace("http:", "https:")
                    AsyncImage(
                        model = fixedThumbnailUrl,
                        contentDescription = "Capa do Livro",
                        modifier = Modifier
                            .width(250.dp)
                            .height(300.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Avaliação por estrelas
                Text("Avaliação:")
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "Avaliação de $i estrelas",
                            tint = if (i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    rating = i
                                    viewModel.updateBookRating(safeBook.id, i) // Atualiza no banco
                                    Toast.makeText(context, "Avaliação salva!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Voltar")
                    }

                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Remover")
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmação") },
                        text = {
                            Text("Deseja mesmo remover do banco?\nEssa ação é irreversível.")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    book?.let {
                                        viewModel.deleteBook(it)
                                        Toast.makeText(
                                            context,
                                            "Livro removido!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack("book_list", inclusive = false)
                                    }
                                    showDialog = false
                                }
                            ) {
                                Text("Sim, desejo")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDialog = false }
                            ) {
                                Text("Não")
                            }
                        }
                    )
                }

            }
        }
    }
}


@Composable
fun BookListScreen(navController: NavController, viewModel: BookViewModel = hiltViewModel()) {
    val books by viewModel.savedBooks.collectAsState()

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
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    "view_book/${book.id}"
                                )
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            book.thumbnail?.let { url ->
                                val fixedThumbnailUrl = url.replace("http:", "https:")
                                AsyncImage(
                                    model = fixedThumbnailUrl,
                                    contentDescription = "Capa do Livro",
                                    modifier = Modifier
                                        .width(250.dp)
                                        .fillMaxWidth()
                                        .aspectRatio(0.75f)
                                        .height(300.dp)
                                )
                            }
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
    var title by remember { mutableStateOf("") }
    val books by viewModel.books.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Buscar Livro", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para ISBN
        TextField(
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text("Digite o ISBN") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.searchBook(isbn) }) {
            Text("Buscar por ISBN")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para título
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Digite o título") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.searchBooksByTitle(title) }) {
            Text("Buscar por Título")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (books.isEmpty()) {
            Text("Nenhum resultado encontrado.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1), // Exibir 2 colunas
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
            ) {
                items(books) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                val bookEntity = BookEntity(
                                    title = book.title,
                                    authors = book.authors?.joinToString(", "),
                                    publisher = book.publisher,
                                    publishedDate = book.publishedDate,
                                    description = book.description,
                                    pageCount = book.pageCount,
                                    thumbnail = book.imageLinks?.thumbnail
                                )
                                viewModel.saveBook(bookEntity)
                                Toast.makeText(
                                    context,
                                    "Livro salvo com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                book.title ?: "Erro ao carregar",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            book.authors?.forEach { author -> Text(author) }
                            book.imageLinks?.thumbnail?.let { url ->
                                AsyncImage(
                                    model = url.replace("http:", "https:"),
                                    contentDescription = "Capa do Livro",
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(150.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
