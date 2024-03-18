package trainer

class Question(
    inputAnswerOptions: MutableSet<Word>,
    val wordToStudy: Word,
) {
    var answerOptions = inputAnswerOptions
        private set
}