fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBot = TelegramBot(botToken)
    var updateId = 0

    val updateIdRegex: Regex = "\"update_id\":(.+[0-9]),".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(.+?),".toRegex()
    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val buttonCallbackDataRegex: Regex = "\"data\":\"(.+?)\"}".toRegex()

    val trainer = LearningWordsTrainer()

    while (true) {
        Thread.sleep(3000)
        val botUpdates = telegramBot.getUpdates(updateId)
        println(botUpdates)

        val lastUpdateId: Int? = getRegexValue(updateIdRegex, botUpdates)?.toIntOrNull()
        val chatId: String? = getRegexValue(chatIdRegex, botUpdates)
        val messageFromChat: String? = getRegexValue(messageRegex, botUpdates)
        val buttonCallbackData: String? = getRegexValue(buttonCallbackDataRegex, botUpdates)

        if ((chatId != null && messageFromChat != null) || (chatId != null && buttonCallbackData != null)) {

            when (messageFromChat?.lowercase()) {
                "hello" -> telegramBot.sendMessage(chatId, "Hello!")
                BOT_COMMAND_START -> telegramBot.sendMenu(chatId)
            }

            when (buttonCallbackData) {
                CALLBACK_DATA_LEARN_WORD -> {
                    val currentQuestion = trainer.getNextQuestion()
                    println(currentQuestion?.wordToStudy ?: 0)
                    currentQuestion?.answerOptions?.forEachIndexed { index, word -> println("${index + 1}. ${word.translation}")}

                    if (currentQuestion == null) {
                        telegramBot.sendMessage(chatId, ALL_THE_WORDS_ARE_LEARNED)
                        telegramBot.sendMenu(chatId)
                    } else telegramBot.sendQuestion(chatId, currentQuestion)
                }

                CALLBACK_DATA_STATISTIC -> {
                    val statistics = trainer.getStatisticsOfLearningWords()
                    val message = "Выучено ${statistics.numberOfLearnedWords} из ${statistics.numberOfWords} слов | " +
                            "${String.format("%.2f", statistics.percentageOfWordsLearned)}%"
                    telegramBot.sendMessage(chatId, message)
                }

                "exit" -> TODO("Завершать работу бота")
            }

            if (buttonCallbackData?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
                val answer = buttonCallbackData.substringAfter("_").toIntOrNull()
                telegramBot.checkNextQuestionAnswer(trainer, chatId, answer)

            }
        }
        updateId = (lastUpdateId ?: continue) + 1
    }
}

fun getRegexValue(regexPattern: Regex, data: String): String? = regexPattern.find(data)?.groups?.get(1)?.value

const val BOT_COMMAND_START = "/start"
const val CALLBACK_DATA_LEARN_WORD = "learn_words_clicked"
const val CALLBACK_DATA_STATISTIC = "statistic_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val ALL_THE_WORDS_ARE_LEARNED = "Вы выучили все слова"