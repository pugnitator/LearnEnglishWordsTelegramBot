package trainer

data class Word(
    val original: String,
    val translation: String,
    var numberOfCorrectAnswers: Int = 0
)