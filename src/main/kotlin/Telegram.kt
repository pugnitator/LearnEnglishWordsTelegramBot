fun main(args: Array<String>) {
    val trainer = LearningWordsTrainer()
    val botToken = args[0]
    val telegramBot = TelegramBot(botToken)
    var updateId = 0

    val updateIdRegex: Regex = "\"update_id\":(.+[0-9]),".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(.+?),".toRegex()
    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val buttonCallbackDataRegex: Regex = "\"data\":\"(.+?)\"}".toRegex()

    while (true) {
        Thread.sleep(3000)
        val botUpdates = telegramBot.getUpdates(updateId)
        println(botUpdates)

        val lastUpdateId: Int? = getRegexValue(updateIdRegex, botUpdates)?.toIntOrNull()
        val chatId: String? = getRegexValue(chatIdRegex, botUpdates)
        val messageFromChat: String? = getRegexValue(messageRegex, botUpdates)
        val buttonCallbackData: String? = getRegexValue(buttonCallbackDataRegex, botUpdates)

        if (messageFromChat.equals("hello", ignoreCase = true) && chatId != null)
            telegramBot.sendMessage(chatId, "Hello!")

        if (messageFromChat.equals("/start", ignoreCase = true) && chatId != null)
            telegramBot.sendMenu(chatId)

        if (buttonCallbackData?.lowercase() == "statistic_clicked" && chatId != null) {
            val statistics = trainer.getStatisticsOfLearningWords()
            val message = "Выучено ${statistics.numberOfLearnedWords} из ${statistics.numberOfWords} слов | " +
                    "${String.format("%.2f", statistics.percentageOfWordsLearned)}%"
            telegramBot.sendMessage(chatId, message)
        }



//        if ((chatId != null && messageFromChat != null) || (chatId != null && buttonCallbackData != null)) {
//            when (messageFromChat?.lowercase()) {
//                "hello" -> telegramBot.sendMessage(chatId, "Hello!")
//                "/start" -> telegramBot.sendMenu(chatId)
//                else -> continue
//            }
//
//            when (buttonCallbackData) {
//                "learn_words_clicked" -> TODO("Отправлять слова для изучения")
//                "statistic_clicked" -> {
//                    val statistics = trainer.getStatisticsOfLearningWords()
//                    val message = "Выучено 10 слов| ,kf,kf"
//                    telegramBot.sendMessage(chatId, message)
//                }
//
//                "stop_bot" -> TODO("Завершать работу бота")
//                else -> continue
//            }
//        }

        updateId = (lastUpdateId ?: continue) + 1

    }
}

fun getRegexValue(regexPattern: Regex, data: String): String? = regexPattern.find(data)?.groups?.get(1)?.value