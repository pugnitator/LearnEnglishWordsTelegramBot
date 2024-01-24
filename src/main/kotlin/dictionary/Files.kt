package org.example.dictionary

import java.io.File

fun main() {
    val dictionary: MutableList<Word> = mutableListOf()
    val wordsFile = File("words.txt")
//    wordsFile.createNewFile()
//    wordsFile.appendText("hello привет")
//    wordsFile.appendText("dog собака")
//    wordsFile.appendText("cat кошка")
    for (line in wordsFile.readLines()) {
        val line = line.split('|')
        dictionary.add(Word(line[0], line[1], line[2].toIntOrNull() ?: 0))
    }
//    dictionary.forEach { println(it) }
    do {
        print("Меню: 1 - Учить слова, 2 - Статистика, 0 - Выход. \nВведите номер нужной операции: ")
        val inputValue = readln().toIntOrNull() ?: 999
        when (inputValue) {
            0 -> continue
            1 -> {
//                do {
                    var listOfUnlearnedWords = dictionary.filter { it.numberOfCorrectAnswers < LIMIT_OF_CORRECT_ANSWER }
                    val numberOfUnlearnedWords = listOfUnlearnedWords.size
                    if (numberOfUnlearnedWords == 0) {
                        println("Вы выучили все слова.")
                        break
                    }
                    do {
                        listOfUnlearnedWords = listOfUnlearnedWords.shuffled()
                        val wordToStudy: Word = listOfUnlearnedWords.last()

                        var answerOptions: List<Word> = dictionary.takeLast(NUMBER_OF_WORDS_DISPLAYED)
                        answerOptions = answerOptions.shuffled()
                        val correctAnswer = answerOptions.indexOf(wordToStudy) + 1

                        println("Введите номер правильного перевода для слова ${wordToStudy.original}. Для выхода введите 0.")
                        answerOptions.shuffled().forEachIndexed { index, word -> println("${index + 1}. ${word.translation}") }

                        val answer = readln().toIntOrNull() ?: 0
                        when (answer) {
                            correctAnswer -> {
                                println("Ответ правильный, отлично!")
                                wordToStudy.numberOfCorrectAnswers += 1
                            }
                            0 -> break
                            else -> println("Неверно, попробуйте ещё раз.")
                        }
                    } while (answer != 0)
//                } while ()
            }

            2 -> {
                val numberOfWords = dictionary.size
                val numberOfLearnedWords =
                    dictionary.filter { it.numberOfCorrectAnswers >= LIMIT_OF_CORRECT_ANSWER }.size
                val percentageOfWordsLearned = numberOfLearnedWords.toFloat() / numberOfWords * 100

                println(
                    "Выучено $numberOfLearnedWords из $numberOfWords слов | ${
                        String.format(
                            "%.2f",
                            percentageOfWordsLearned
                        )
                    }%"
                )
                println()
            }

            else -> {
                println("Введено неизвестное значение, попробуйте снова.\n")
                continue
            }
        }

    } while (inputValue != 0)

}

const val LIMIT_OF_CORRECT_ANSWER = 3
const val NUMBER_OF_WORDS_DISPLAYED = 4