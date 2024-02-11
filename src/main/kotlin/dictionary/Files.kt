package org.example.dictionary

import java.io.File

fun main() {
    val dictionary: MutableSet<Word> = mutableSetOf()
    val wordsFile = File("words.txt")
    for (line in wordsFile.readLines()) {
        val line = line.split('|')
        dictionary.add(Word(line[0], line[1], line[2].toIntOrNull() ?: 0))
    }

    do {
        print("Меню: 1 - Учить слова, 2 - Статистика, 0 - Выход. \nВведите номер нужной операции: ")
        val inputValue = readln().toIntOrNull()
        when (inputValue) {
            0 -> continue
            1 -> {
                do {
                    val listOfUnlearnedWords = dictionary.filter { it.numberOfCorrectAnswers < LIMIT_OF_CORRECT_ANSWER }
                    if (listOfUnlearnedWords.isEmpty()) {
                        println("Вы выучили все слова.")
                        break
                    }

                    // выбираем список из 4х слов, перевод одного из которых будем запрашивать
                    var answerOptions =
                        listOfUnlearnedWords.shuffled().takeLast(NUMBER_OF_WORDS_DISPLAYED).toMutableSet()

                    // определяем слово, которое будем изучать в этом цикле
                    val wordToStudy: Word = answerOptions.random()

                    // дополняем список недостающими словами, если нужно
                    if (answerOptions.size < NUMBER_OF_WORDS_DISPLAYED) {
                        val numberOfMissingWords = NUMBER_OF_WORDS_DISPLAYED - answerOptions.size
                        answerOptions.addAll(dictionary.filter { it.numberOfCorrectAnswers >= LIMIT_OF_CORRECT_ANSWER }
                            .shuffled().takeLast(numberOfMissingWords))
                    }
                    var answer: Int

                    //запрашиваем перевод отобранного для изучения слова
                    do {
                        println("Введите номер правильного перевода для слова ${wordToStudy.original}. Для выхода введите 0.")

                        answerOptions = answerOptions.shuffled().toMutableSet()
                        answerOptions.forEachIndexed { index, word -> println("${index + 1}. ${word.translation}") }
                        val correctAnswer = answerOptions.indexOf(wordToStudy) + 1

                        answer = readln().toIntOrNull() ?: 0
                        when (answer) {
                            correctAnswer -> {
                                println("Ответ правильный, отлично!")
                                wordToStudy.numberOfCorrectAnswers += 1
                                wordsFile.saveDictionary(dictionary)
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
                    "Выучено $numberOfLearnedWords из $numberOfWords слов | " +
                            "${String.format("%.2f", percentageOfWordsLearned)}%"
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

fun File.saveDictionary(dictionary: Set<Word>) {
    writeText("")
    for (i in dictionary) {
        appendText("${i.original}|${i.translation}|${i.numberOfCorrectAnswers}\n")
    }
}

const val LIMIT_OF_CORRECT_ANSWER = 3
const val NUMBER_OF_WORDS_DISPLAYED = 4