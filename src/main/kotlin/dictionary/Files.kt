@file:Suppress("UNREACHABLE_CODE")

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
                val listOfUnlearnedWords = dictionary.filter { it.numberOfCorrectAnswers < LIMIT_OF_CORRECT_ANSWER }
                if (listOfUnlearnedWords.isEmpty()) {
                    println("Вы выучили все слова.")
                    break
                }

                do {
                    // выбираем список из 4х слов, перевод одного из которых будем запрашивать
                    var answerOptions : List<Word> = listOfUnlearnedWords.shuffled().takeLast(NUMBER_OF_WORDS_DISPLAYED)
//                    TODO("Что делаем, если у нас менее $NUMBER_OF_WORDS_DISPLAYED слов в списке?")

                    // определяем слово, которое будем изучать в этом цикле
                    val wordToStudy: Word = answerOptions.random()
                    var answer: Int

                    //запрашиваем перевод одного и того же слова до тех пор, пока не будет введён верный ответ
                    do{
                        println("Введите номер правильного перевода для слова ${wordToStudy.original}. Для выхода введите 0.")

                        answerOptions = answerOptions.shuffled() // при неверном ответе переводы будут в новом порядке
                        answerOptions.forEachIndexed { index, word -> println("${index + 1}. ${word.translation}") }
                        val correctAnswer = answerOptions.indexOf(wordToStudy) + 1

                        answer = readln().toIntOrNull() ?: 0
                        when (answer) {
                            correctAnswer -> {
                                println("Ответ правильный, отлично!")
                                wordToStudy.numberOfCorrectAnswers += 1
                            }
                            0 -> break
                            else -> println("Неверно, попробуйте ещё раз.")
                        }
                    } while (answer != correctAnswer)

                } while (answer != 0)
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