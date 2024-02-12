package org.example.dictionary

fun main() {

    val trainer = LearningWordsTrainer()

    do {
        print("Меню: 1 - Учить слова, 2 - Статистика, 0 - Выход. \nВведите номер нужной операции: ")
        val inputValue = readln().toIntOrNull()
        when (inputValue) {
            0 -> continue
            1 -> {
                do {
                    if (trainer.listOfUnlearnedWords.isEmpty()) {
                        println("Вы выучили все слова.")
                        break
                    }

                    val question = trainer.getNextQuestion()
                    var userAnswer: Int

                    do {
                        println("Введите номер правильного перевода для слова ${question.wordToStudy.original}. Для выхода введите 0.")
                        question.printAnswerOptions()
                        userAnswer = readln().toIntOrNull() ?: 0
                        if (userAnswer == 0) break
                        val isAnswerCorrect = trainer.isAnswerCorrect(question, userAnswer)
                    } while (!isAnswerCorrect)

                } while (userAnswer != 0)
            }

            2 -> {
                val statistics = trainer.getStatisticsOfLearningWords()
                println(
                    "Выучено ${statistics.numberOfLearnedWords} из ${statistics.numberOfWords} слов | " +
                            "${String.format("%.2f", statistics.percentageOfWordsLearned)}%"
                )
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