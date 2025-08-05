data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val summary: String,
    val chapters: List<Chapter>
)