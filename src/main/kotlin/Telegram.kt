import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBot = TelegramBot(botToken)
    val trainer = LearningWordsTrainer()
    var nextUpdateId = 0L

    val json = Json {
        ignoreUnknownKeys = true
    }

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramBot.getUpdates(nextUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString<Response>(responseString)
        val updates = response.result
        val lastUpdate = updates.lastOrNull() ?: continue

        val lastUpdateId : Long = lastUpdate.updateId
        nextUpdateId = lastUpdateId + 1

        val chatId: Long = lastUpdate.message?.chat?.id ?: lastUpdate.callbackQuery?.message?.chat?.id ?: continue
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

                CALLBACK_DATA_TO_MENU -> telegramBot.sendMenu(chatId)
            }

            if (buttonCallbackData.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
                val answer = buttonCallbackData.substringAfter("_").toIntOrNull() ?: continue
                telegramBot.checkNextQuestionAnswer(trainer, chatId, answer)
            }
        }
    }
}

const val BOT_COMMAND_START = "/start"

const val CALLBACK_DATA_LEARN_WORD = "learn_words_clicked"
const val CALLBACK_DATA_STATISTIC = "statistic_clicked"
const val CALLBACK_DATA_TO_MENU = "to_menu_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

const val ALL_THE_WORDS_ARE_LEARNED = "Вы выучили все слова"