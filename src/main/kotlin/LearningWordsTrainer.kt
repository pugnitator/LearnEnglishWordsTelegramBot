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
    fun printAnswerOptions(){
        answerOptions = answerOptions.shuffled().toMutableSet()
        answerOptions.forEachIndexed { index, word -> println("${index + 1}. ${word.translation}") }
    }
}

class LearningWordsTrainer(
    private val limitOfCorrectAnswers: Int = 3,
    private val numberOfWordsToDisplayed: Int = 4,
    ) {
    private val dictionary = loadDictionary()
    val listOfUnlearnedWords = dictionary.filter { it.numberOfCorrectAnswers < limitOfCorrectAnswers }

    private fun loadDictionary() : Set<Word> {
        try{
        val dictionary: MutableSet<Word> = mutableSetOf()
        val wordsFile = File("words.txt")
        for (line in wordsFile.readLines()) {
            val line = line.split('|')
            dictionary.add(Word(line[0], line[1], line[2].toIntOrNull() ?: 0))
        }
        return dictionary}catch (e: IndexOutOfBoundsException){
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary() {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        for (i in dictionary) {
            wordsFile.appendText("${i.original}|${i.translation}|${i.numberOfCorrectAnswers}\n")
        }
    }

    fun getStatisticsOfLearningWords() : Statistics{
        val numberOfWords = dictionary.size
        val numberOfLearnedWords =
            dictionary.filter { it.numberOfCorrectAnswers >= limitOfCorrectAnswers }.size
        val percentageOfWordsLearned = numberOfLearnedWords.toFloat() / numberOfWords * 100
        return Statistics(numberOfWords,numberOfLearnedWords,percentageOfWordsLearned)
    }

    fun getNextQuestion() : Question {
        val answerOptions =
            listOfUnlearnedWords.shuffled().takeLast(numberOfWordsToDisplayed).toMutableSet()
        val wordToStudy = answerOptions.random()

        if (answerOptions.size < numberOfWordsToDisplayed) {
            val numberOfMissingWords = numberOfWordsToDisplayed - answerOptions.size
            answerOptions.addAll(dictionary.filter { it.numberOfCorrectAnswers >= limitOfCorrectAnswers }
                .shuffled().takeLast(numberOfMissingWords))
        }

        return Question(answerOptions, wordToStudy)
    }

    fun isAnswerCorrect(question: Question, userAnswer: Int?) : Boolean{
        val isAnswerCorrect: Boolean
        val correctAnswer = question.answerOptions.indexOf(question.wordToStudy) + 1
        when (userAnswer) {
            correctAnswer -> {
                isAnswerCorrect = true
                println("Ответ правильный, отлично!")
                question.wordToStudy.numberOfCorrectAnswers += 1
                saveDictionary()
            }
            else -> {
                isAnswerCorrect = false
                println("Неверно, попробуйте ещё раз.")
            }
        }
        return isAnswerCorrect
    }

}