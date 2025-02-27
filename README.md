# 📚 Catálogo Virtual de Livros

Um aplicativo Android simples para organizar e catalogar sua biblioteca pessoal, permitindo adicionar livros, avaliar leituras e criar uma lista de desejos.

## ✨ Funcionalidades
- Adicionar livros manualmente ou via ISBN
- Consultar informações automáticas via API (Open Library ou Google Books)
- Avaliar livros lidos
- Criar uma lista de desejos
- Interface moderna usando Jetpack Compose
- Banco de dados local via Room Database

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Kotlin
- **UI:** Jetpack Compose
- **Banco de Dados:** Room (SQLite)
- **Consumo de API:** Retrofit
- **IDE:** Android Studio

## ✅ Como Instalar e Executar
1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/catalogo-livros.git
   cd catalogo-livros
   ```
2. **Abra o projeto no Android Studio**
3. **Compile e execute em um emulador ou dispositivo real**

## 📈 Estrutura do Projeto
```
/catalogo-livros
│-- app/
│   │-- src/main/
│   │   │-- java/com/seuusuario/catalogo/
│   │   │   │-- ui/  # Telas e componentes UI
│   │   │   │-- data/  # Models e banco de dados Room
│   │   │   │-- network/  # Consumo de API (ISBN)
│   │   │   │-- viewmodel/  # Lógica de negócio e estados da UI
│   │-- AndroidManifest.xml
│-- README.md
```

## 🌟 Melhorias Futuras
- Suporte a exportação de dados
- Sincronização em nuvem (Google Drive)
- Sugestão de livros baseada no histórico

## 🚀 Contribuição
Este é um projeto pessoal, mas se desejar sugerir melhorias, abra um **Issue** ou um **Pull Request**.

## 📝 Licença
Este projeto é de uso pessoal e não possui uma licença específica.

---
