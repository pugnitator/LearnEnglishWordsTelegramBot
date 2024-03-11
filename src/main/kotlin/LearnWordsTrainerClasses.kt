import java.io.File
import java.lang.IndexOutOfBoundsException

data class Word(
    val original: String,
    val translation: String,
    var numberOfCorrectAnswers: Int = 0
)

data class Statistics(
    val numberOfWords: Int,
    val numberOfLearnedWords: Int,
    val percentageOfWordsLearned: Float,
)

class Question(
    inputAnswerOptions: MutableSet<Word>,
    val wordToStudy: Word,
) {
    var answerOptions = inputAnswerOptions
        private set
}

class LearnWordsTrainer(
    private val limitOfCorrectAnswers: Int = 3,
    private val numberOfWordsToDisplayed: Int = 4,
    private val wordsFileName: String = "words.txt",
) {
    private val dictionary = loadDictionary()
    var currentQuestion: Question? = null
        private set

    private fun loadDictionary(): Set<Word> {
        try {
            val dictionary: MutableSet<Word> = mutableSetOf()
            val wordsFile = File(wordsFileName)
            if (!wordsFile.exists()) {
                File("words.txt").copyTo(wordsFile)
            }
            for (line in wordsFile.readLines()) {
                val splitLine = line.split('|')
                dictionary.add(Word(splitLine[0], splitLine[1], splitLine[2].toIntOrNull() ?: 0))
            }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary() {
        val wordsFile = File(wordsFileName)
        wordsFile.writeText("")
        for (i in dictionary) {
            wordsFile.appendText("${i.original}|${i.translation}|${i.numberOfCorrectAnswers}\n")
        }
    }

    private fun getListOfLearnedWords() = dictionary.filter { it.numberOfCorrectAnswers >= limitOfCorrectAnswers }

    private fun getListOfUnlearnedWords() = dictionary.filter { it.numberOfCorrectAnswers < limitOfCorrectAnswers }

    fun getNextQuestion(): Question? {
        var answerOptions = getListOfUnlearnedWords().shuffled().takeLast(numberOfWordsToDisplayed).toMutableSet()
        if (answerOptions.isEmpty()) currentQuestion = null
        else {
            val wordToStudy = answerOptions.random()
            if (answerOptions.size < numberOfWordsToDisplayed) {
                val numberOfMissingWords = numberOfWordsToDisplayed - answerOptions.size

                answerOptions.addAll(getListOfLearnedWords().shuffled().takeLast(numberOfMissingWords))
                answerOptions = answerOptions.shuffled().toMutableSet()
            }
            currentQuestion = Question(answerOptions, wordToStudy)
        }
        return currentQuestion
    }

    fun isAnswerCorrect(userAnswer: Int?): Boolean {
        val isAnswerCorrect: Boolean
        if (currentQuestion == null) isAnswerCorrect = false
        else {
            val correctAnswer = currentQuestion!!.answerOptions.indexOf(currentQuestion!!.wordToStudy)
            isAnswerCorrect = userAnswer == correctAnswer
            if (isAnswerCorrect) {
                currentQuestion!!.wordToStudy.numberOfCorrectAnswers += 1
                saveDictionary()
            }
        }
        return isAnswerCorrect
    }

    fun getStatisticsOfLearningWords(): Statistics {
        val numberOfWords = dictionary.size
        val numberOfLearnedWords = getListOfLearnedWords().size
        val percentageOfWordsLearned = numberOfLearnedWords.toFloat() / numberOfWords * 100
        return Statistics(numberOfWords, numberOfLearnedWords, percentageOfWordsLearned)
    }

    fun resetStatisticsOfLearningWords() {
        dictionary.forEach { it.numberOfCorrectAnswers = 0 }
        saveDictionary()
    }
}