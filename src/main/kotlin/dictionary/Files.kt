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
                do {
                    val numberOfUnlearnedWords = dictionary.filter { it.numberOfCorrectAnswers < 3 }.size
                    if (numberOfUnlearnedWords == 0) {
                        println("Вы выучили все слова.")
                        break
                    }
                    val wordToStudy: Word = dictionary.filter { it.numberOfCorrectAnswers < 3 }.shuffled().last()
                    var answerOptions: List<Word> = dictionary.takeLast(5)
                    println("Введите номер правильного перевода для слова ${wordToStudy.original}:")
                    do {
                        answerOptions = answerOptions.shuffled()
                        val correctAnswer = answerOptions.indexOf(wordToStudy) + 1
                        answerOptions.forEachIndexed { index, word -> println("${index + 1}. ${word.translation}") }
                        val answer = readln().toIntOrNull() ?: 0
                        val isAnswerCorrect = answer == correctAnswer
                        when (isAnswerCorrect) {
                            true -> {
                                println("Ответ правильный, отлично!")
                                wordToStudy.numberOfCorrectAnswers += 1
                            }

                            else -> println("Неверно, попробуйте ещё раз.")
                        }
                    } while (!isAnswerCorrect)

                    println("Продолжить изучение слов? Выберите нужный вариант: 1 - Да, 0 - Нет.")
                    val isHeWantToContinue = readln().toIntOrNull() ?: 0
                    when (isHeWantToContinue) {
                        1 -> continue
                        else -> break
                    }

                } while (isHeWantToContinue == 1)
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

