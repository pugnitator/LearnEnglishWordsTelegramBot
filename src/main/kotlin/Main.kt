fun main() {
    val trainer = try {
        LearningWordsTrainer(3, 4)
    } catch (e: Exception) {
        println("Не удалось загрузить словарь.")
        return
    }

    do {
        print("Меню: 1 - Учить слова, 2 - Статистика, 0 - Выход. \nВведите номер нужной операции: ")
        val inputValue = readln().toIntOrNull()
        when (inputValue) {
            0 -> continue
            1 -> {
                do {
                    val currentQuestion = trainer.getNextQuestion()
                    var userAnswer: Int?

                    if (currentQuestion == null) {
                        println("Вы выучили все слова.")
                        break
                    } else {
                        do {
                            println(
                                "Введите номер правильного перевода для слова ${currentQuestion.wordToStudy.original}. " +
                                        "Для выхода введите 0."
                            )
                            currentQuestion.shuffledAnswerOptions()
                            currentQuestion.answerOptions.forEachIndexed { index, word -> println("${index + 1}. ${word.translation}") }
                            userAnswer = readln().toIntOrNull()

                            if (userAnswer == 0) break
                            val isAnswerCorrect = trainer.isAnswerCorrect(userAnswer)

                            if (isAnswerCorrect) println("Ответ правильный, отлично!")
                            else println("Неверно, попробуйте ещё раз.")

                        } while (!isAnswerCorrect)
                    }
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