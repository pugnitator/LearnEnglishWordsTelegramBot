package org.example.dictionary

import java.io.File
import java.util.Collections.shuffle

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
                do {
                    shuffle(dictionary.filter { it.numberOfCorrectAnswers < 3 })
                    val wordToStudy: Word = dictionary.last()
                    val answerOptions: List<Word> = dictionary.takeLast(5)
                    println("Выберите правильный перевод для слова ${wordToStudy.original} из вариантов ниже:")
                    do {
                        shuffle(answerOptions)
                        answerOptions.forEach { println(it.translation) }
                        val answer = readln()
                        val isAnswerCorrect = answer == wordToStudy.translation
                        when (isAnswerCorrect) {
                            true -> {
                                println("Ответ правильный, отлично!")
                                wordToStudy.numberOfCorrectAnswers += 1
                            }
                            else -> println("Неверно, попробуйте ещё раз.")
                        }
                    } while (!isAnswerCorrect)

                    println("Продолжить изучение слов? Выберите нужный вариант: 1 - Да, 0 - Нет.")
                    var isHeWantToContinue = readln().toIntOrNull() ?: 0
                    if (isHeWantToContinue != 1) isHeWantToContinue = 0

                } while (isHeWantToContinue == 1)
//                for (i in dictionary.filter { it.numberOfCorrectAnswers < 3 }) {
//                    do {
//                        println("Выберите правильный перевод для слова ${i.original} из вариантов ниже:")
//                        val answerOptions: MutableList<Word> = dictionary.filter { it != i }.shuffled().take(3).toMutableList()
//                        answerOptions.add(i)
//                        println(answerOptions)
//
//                        shuffle(answerOptions)
//                        println(answerOptions)
//                        answerOptions.forEach { it.translation }
//                        val answer = readln().toIntOrNull() ?: 0
//                        when (answer) {
//                            0 -> {
//                                println("Введно несуществующее значение, попробуйте снова.")
//                                continue
//
//                            }
//                        }
//                    } while ()
            }

            2 -> {
                val numberOfWords = dictionary.size
                val numberOfLearnedWords = dictionary.filter { it.numberOfCorrectAnswers >= 3 }.size
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

