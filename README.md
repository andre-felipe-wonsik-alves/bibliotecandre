# 📚 Bibliotecandre

**Bibliotecandre** é um aplicativo pessoal para organizar e visualizar os livros que você já leu ou deseja ler. Desenvolvido em Jetpack Compose, é leve, rápido e funciona offline, utilizando banco de dados local.

## ✨ Funcionalidades

### ✅ Listagem de livros salvos
- Exibe os livros salvos em um **grid responsivo com 2 colunas**.
- Cada item apresenta a **capa do livro** (ou um retângulo substituto caso não haja thumbnail).
- Os livros são clicáveis e direcionam para uma tela de visualização detalhada.

### 🔍 Barra de pesquisa
- Barra de pesquisa com design circular e placeholder dinâmico:
  > "Pesquise algo entre os X livros que você já leu!"
- Permite filtrar os livros listados por título ou autor.
- Input otimizado com altura reduzida para manter a estética clean.

### 📈 Contador de livros
- Exibe a **quantidade total de livros salvos** no banco de dados.
- O valor é utilizado dinamicamente no placeholder da barra de busca.

### ➕ Adicionar livros
- Botão flutuante para adicionar manualmente um novo livro.

### 📷 Escanear ISBN
- Botão flutuante com acesso direto ao **scanner de código de barras (ISBN)** via Google Lens.
- Redireciona para busca automática na Open Library ou Google Books.

### 📖 Visualização de livro
- Tela com detalhes completos do livro:
  - Capa (ou retângulo substituto)
  - Título, autor, editora, data de publicação
  - Descrição com box rolável, caso seja longa
  - Avaliação em estrelas (1 a 5), com persistência no banco local

### 🗑️ Remover livro
- Botão para deletar o livro com **alerta de confirmação**.

---

## 🛠️ Tecnologias

- **Jetpack Compose**
- **Room (SQLite)**
- **Hilt (DI)**
- **Kotlin**
- **Coil (AsyncImage)**
- **Navigation Compose**

---

## 📱 Uso

O app é ideal para leitura em tablets Android. Pode ser utilizado completamente offline após os livros serem adicionados.

---

# Feito como objeto de estudo, portanto, sujeito a erros!
