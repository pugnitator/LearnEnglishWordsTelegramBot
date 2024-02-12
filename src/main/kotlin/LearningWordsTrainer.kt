package org.example.dictionary

import java.io.File

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

class LearningWordsTrainer {

    val dictionary = loadDictionary()
    val listOfUnlearnedWords = dictionary.filter { it.numberOfCorrectAnswers < LIMIT_OF_CORRECT_ANSWER }


    private fun loadDictionary() : Set<Word> {
        val dictionary: MutableSet<Word> = mutableSetOf()
        val wordsFile = File("words.txt")
        for (line in wordsFile.readLines()) {
            val line = line.split('|')
            dictionary.add(Word(line[0], line[1], line[2].toIntOrNull() ?: 0))
        }
        return dictionary
    }

    fun saveDictionary() {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        for (i in dictionary) {
            wordsFile.appendText("${i.original}|${i.translation}|${i.numberOfCorrectAnswers}\n")
        }
    }

    fun getStatisticsOfLearningWords() : Statistics{
        val numberOfWords = dictionary.size
        val numberOfLearnedWords =
            dictionary.filter { it.numberOfCorrectAnswers >= LIMIT_OF_CORRECT_ANSWER }.size
        val percentageOfWordsLearned = numberOfLearnedWords.toFloat() / numberOfWords * 100

        println(
            "Выучено $numberOfLearnedWords из $numberOfWords слов | " +
                    "${String.format("%.2f", percentageOfWordsLearned)}%"
        )
        return Statistics(numberOfWords,numberOfLearnedWords,percentageOfWordsLearned)
    }

    fun getNextQuestion() : Question {
        val answerOptions =
            listOfUnlearnedWords.shuffled().takeLast(NUMBER_OF_WORDS_DISPLAYED).toMutableSet()
        val wordToStudy = answerOptions.random()

        if (answerOptions.size < NUMBER_OF_WORDS_DISPLAYED) {
            val numberOfMissingWords = NUMBER_OF_WORDS_DISPLAYED - answerOptions.size
            answerOptions.addAll(dictionary.filter { it.numberOfCorrectAnswers >= LIMIT_OF_CORRECT_ANSWER }
                .shuffled().takeLast(numberOfMissingWords))
        }

        return Question(answerOptions, wordToStudy)
    }

    fun isAnswerCorrect(question: Question, userAnswer: Int) : Boolean{
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