package org.example.dictionary

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
    var answerOptions: MutableSet<Word>,
    val wordToStudy: Word,
) {
    fun shuffledAnswerOptions(): MutableSet<Word> = answerOptions.shuffled().toMutableSet()
}

class LearningWordsTrainer(
    private val limitOfCorrectAnswers: Int = 3,
    private val numberOfWordsToDisplayed: Int = 4,
    private val wordsFileName: String = "words.txt",
) {
    private val dictionary = loadDictionary()
    val currentQuestion: Question = getNextQuestion()

    private fun loadDictionary(): Set<Word> {
        try {
            val dictionary: MutableSet<Word> = mutableSetOf()
            val wordsFile = File(wordsFileName)
            for (line in wordsFile.readLines()) {
                val line = line.split('|')
                dictionary.add(Word(line[0], line[1], line[2].toIntOrNull() ?: 0))
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

    fun getListOfUnlearnedWords() = dictionary.filter { it.numberOfCorrectAnswers < limitOfCorrectAnswers }

    fun getNextQuestion(): Question {
        var answerOptions = getListOfUnlearnedWords().shuffled().takeLast(numberOfWordsToDisplayed).toMutableSet()
        val wordToStudy = answerOptions.randomOrNull()

        if (answerOptions.size < numberOfWordsToDisplayed) {
            val numberOfMissingWords = numberOfWordsToDisplayed - answerOptions.size

            answerOptions.addAll(getListOfLearnedWords().shuffled().takeLast(numberOfMissingWords))
            answerOptions = answerOptions.shuffled().toMutableSet()
        }
        return Question(answerOptions, wordToStudy)
    }

//    fun isAnswerCorrect(question: Question, userAnswer: Int?): Boolean {
//        val correctAnswer = question.answerOptions.indexOf(question.wordToStudy) + 1
//        val isAnswerCorrect = userAnswer == correctAnswer
//        if (isAnswerCorrect) {
//            question.wordToStudy.numberOfCorrectAnswers += 1
//            saveDictionary()
//        }
//        return isAnswerCorrect
//    }

    fun isAnswerCorrect(userAnswer: Int?): Boolean {
        val correctAnswer = currentQuestion.answerOptions.indexOf(currentQuestion.wordToStudy) + 1
        val isAnswerCorrect = userAnswer == correctAnswer
        if (isAnswerCorrect) {
            currentQuestion.wordToStudy.numberOfCorrectAnswers += 1
            saveDictionary()
        }
        return isAnswerCorrect
    }

    fun getStatisticsOfLearningWords(): Statistics {
        val numberOfWords = dictionary.size
        val numberOfLearnedWords = getListOfLearnedWords().size
        val percentageOfWordsLearned = numberOfLearnedWords.toFloat() / numberOfWords * 100
        return Statistics(numberOfWords, numberOfLearnedWords, percentageOfWordsLearned)
    }
}
