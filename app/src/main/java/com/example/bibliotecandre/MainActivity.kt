package com.example.bibliotecandre

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.Manifest
import android.app.Activity
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bibliotecandre.view.BookViewModel
import com.example.bibliotecandre.data.local.BookEntity
import com.example.bibliotecandre.ui.theme.BibliotecandreTheme
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign

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
        composable("add_book/{isbn}", arguments = listOf(
            navArgument("isbn") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )) { backStackEntry ->
            val isbn = backStackEntry.arguments?.getString("isbn")
            AddBookScreen(navController, isbn)
        }
        composable("view_book/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: -1
            ViewBookScreen(navController, bookId)
        }
        composable("scan_isbn") {
            ScanISBNScreen(navController)
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
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            val circleColor = MaterialTheme.colorScheme.primary
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                drawCircle(
                    color = circleColor,
                    radius = canvasWidth * 1.2f,
                    center = Offset(
                        x = canvasWidth / 2,
                        y = canvasHeight * 0.75f
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .navigationBarsPadding(), // padding para barra inferior
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                book?.let { safeBook ->

                    // Capa com retângulo de fundo
                    safeBook.thumbnail?.let {
                        val fixedThumbnailUrl = it.replace("http:", "https:")
                        Box(
                            modifier = Modifier
                                .background(color=MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(5.dp))
                                .padding(4.dp)
                                .height(350.dp)
                        ) {
                            AsyncImage(
                                model = fixedThumbnailUrl,
                                contentDescription = "Capa do Livro",
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(300.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(safeBook.title ?: "Sem título", style = MaterialTheme.typography.headlineSmall)
                    Text(safeBook.authors ?: "Sem autor", style = MaterialTheme.typography.bodyLarge)
                    Text(safeBook.publisher ?: "Sem editora", style = MaterialTheme.typography.bodyLarge)
                    Text("${safeBook.publishedDate}", style = MaterialTheme.typography.bodyLarge)
                    val descriptionText = safeBook.description ?: "Sem descrição"
                    val wordCount = descriptionText.trim().split("\\s+".toRegex()).size

                    Spacer(modifier = Modifier.height(8.dp))

                    if (wordCount > 80) {
                        Box(
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .background(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = descriptionText,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Justify
                            )
                        }
                    } else {
                        Text(
                            text = descriptionText,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Justify
                        )
                    }



                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Avaliação:")
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.Star,
                                contentDescription = "Avaliação de $i estrelas",
                                tint = if (i <= rating) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        rating = i
                                        viewModel.updateBookRating(safeBook.id, i)
                                        Toast.makeText(context, "Avaliação salva!", Toast.LENGTH_SHORT).show()
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp)) // espaço extra ao fim da tela
                }

            }

            // Botão Voltar
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Botão Deletar
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmação") },
                    text = { Text("Deseja mesmo remover do banco?\nEssa ação é irreversível.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                book?.let {
                                    viewModel.deleteBook(it)
                                    Toast.makeText(context, "Livro removido!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack("book_list", inclusive = false)
                                }
                                showDialog = false
                            }
                        ) {
                            Text("Sim, desejo")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Não")
                        }
                    }
                )
            }
        }
    }
}



@Composable
fun BookListScreen(navController: NavController, viewModel: BookViewModel = hiltViewModel()) {
    val books by viewModel.savedBooks.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Bibliotecandre", style = MaterialTheme.typography.headlineMedium)
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
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(books) { book ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("view_book/${book.id}")
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

        Column(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 64.dp)
                .align(Alignment.BottomStart),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { navController.navigate("add_book/0") },
                modifier = Modifier.size(48.dp).offset(x = 7.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar livro", modifier = Modifier.size(24.dp))
            }
            FloatingActionButton(
                onClick = { navController.navigate("scan_isbn") },
                modifier = Modifier.size(64.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Scan ISBN", modifier = Modifier.size(32.dp))
            }
        }
    }
}


@Composable
fun ScanISBNScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val cameraPermission = remember { mutableStateOf(false) }
    val barcodeScanner = BarcodeScanning.getClient()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermission.value = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Permissão da câmera negada", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            cameraPermission.value = true
        }
    }

    if (cameraPermission.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LaunchedEffect(Unit) {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }


                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                barcodeScanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { isbn ->
                                                navController.navigate("add_book/$isbn")
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("BarcodeScanner", "Erro ao escanear código", e)
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e("ScanISBNScreen", "Erro ao iniciar câmera", exc)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
            }

            Text(
                text = "Aponte para o código de barras do livro!",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp),
                color = MaterialTheme.colorScheme.background,
            )
        }

    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Aguardando permissão da câmera...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun AddBookScreen(
    navController: NavController,
    isbnArg: String? = "",
    viewModel: BookViewModel = hiltViewModel()
) {
    var isbn by remember { mutableStateOf(isbnArg ?: "") }
    var title by remember { mutableStateOf("") }
    val books by viewModel.books.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isbn) {
        if (isbn.isNotBlank()) {
            viewModel.searchBook(isbn)
        }
    }

    LaunchedEffect(title) {
        if (title.isNotBlank()) {
            viewModel.searchBooksByTitle(title)
        }
    }

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
                            if (book.imageLinks?.thumbnail != null) {
                                book.imageLinks?.thumbnail?.let { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Capa do Livro",
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(150.dp),
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = "Ícone de livro",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}