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
                "/start" -> telegramBot.sendMenu(chatId)
            }

            when (buttonCallbackData) {
                CD_LEARN_WORD -> {

                    val currentQuestion = trainer.getNextQuestion()

                    if (currentQuestion == null) {
                        telegramBot.sendMessage(chatId, "Вы выучили все слова.")
                        telegramBot.sendMenu(chatId)
                    } else telegramBot.sendQuestion(chatId, currentQuestion)
                }

                CD_STATISTIC -> {
                    val statistics = trainer.getStatisticsOfLearningWords()
                    val message = "Выучено ${statistics.numberOfLearnedWords} из ${statistics.numberOfWords} слов | " +
                            "${String.format("%.2f", statistics.percentageOfWordsLearned)}%"
                    telegramBot.sendMessage(chatId, message)
                }

                "stop_bot" -> TODO("Завершать работу бота")
            }

//            if (buttonCallbackData!!.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
//
//            }
        }


        updateId = (lastUpdateId ?: continue) + 1

    }
}

fun getRegexValue(regexPattern: Regex, data: String): String? = regexPattern.find(data)?.groups?.get(1)?.value

const val CD_LEARN_WORD = "learn_words_clicked"
const val CD_STATISTIC = "statistic_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"