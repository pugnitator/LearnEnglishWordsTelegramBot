
fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBot = TelegramBot(botToken)
    val trainers = HashMap<Long, LearnWordsTrainer>()

    var lastUpdateId = 0L

    while (true) {
        Thread.sleep(800)
        val response = telegramBot.getUpdates(lastUpdateId)

        if (!response.ok) {
            println("Ошибка получения апдейта.")
            continue
        } else {
            val updates = response.result
            if (updates.isEmpty()) continue

            val sortedUpdates = updates.sortedBy { it.updateId }

            val updatesForEachChatId: Map<Long, List<Update>> =
                sortedUpdates.filter { it.chatId != null }.groupBy { it.chatId!! }
            handleUpdate(telegramBot, updatesForEachChatId, trainers)

            lastUpdateId = sortedUpdates.last().updateId + 1
        }
    }
}

fun handleUpdate(telegramBot: TelegramBot, updatesForEachChatId: Map<Long, List<Update>>, trainers: HashMap<Long, LearnWordsTrainer>) {
    val lastUpdate = updatesForEachChatId.values.last().last()
    val chatId = lastUpdate.chatId ?: return
    val trainer = trainers.getOrPut(chatId) {
        LearnWordsTrainer(3, 4, "$chatId.txt")
    }

    val messageFromChat: String? = lastUpdate.message?.text
    val buttonCallbackData: String? = lastUpdate.callbackQuery?.data

    if (messageFromChat != null && messageFromChat.lowercase() == BOT_COMMAND_START) telegramBot.sendMenu(chatId)

    if (buttonCallbackData != null) {
        when (buttonCallbackData) {
            CALLBACK_DATA_LEARN_WORD -> telegramBot.getNextQuestion(trainer, chatId)

            CALLBACK_DATA_STATISTIC -> {
                val statistics = trainer.getStatisticsOfLearningWords()
                val message = "Выучено ${statistics.numberOfLearnedWords} из ${statistics.numberOfWords} слов | " +
                        "${String.format("%.2f", statistics.percentageOfWordsLearned)}%"
                telegramBot.sendMessage(chatId, message)
            }

            CALLBACK_DATA_RESET_STATISTIC -> {
                trainer.resetStatisticsOfLearningWords()
                telegramBot.sendMessage(chatId, "Прогресс сброшен.")
            }

            CALLBACK_DATA_TO_MENU -> telegramBot.sendMenu(chatId)
        }

        if (buttonCallbackData.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
            val answer = buttonCallbackData.substringAfter("_").toIntOrNull() ?: return
            telegramBot.checkNextQuestionAnswer(trainer, chatId, answer)
        }
    }
}