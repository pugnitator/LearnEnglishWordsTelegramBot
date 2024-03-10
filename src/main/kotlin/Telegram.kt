import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBot = TelegramBot(botToken)
    val trainer = LearningWordsTrainer()
    var lastUpdateId = 0L

    val json = Json {ignoreUnknownKeys = true}

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramBot.getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString<Response>(responseString)
        val updates = response.result
        if (updates.isEmpty()) continue
        val sortedUpdates = updates.sortedBy { it.updateId }
        val updatesForEachChatId: Map<Long, List<Update>> = sortedUpdates.filter { it.chatId != null }.groupBy { it.chatId!! }
        telegramBot.handleUpdate(updatesForEachChatId, trainer)

        lastUpdateId = sortedUpdates.last().updateId + 1

    }
}

const val BOT_COMMAND_START = "/start"

const val CALLBACK_DATA_LEARN_WORD = "learn_words_clicked"
const val CALLBACK_DATA_STATISTIC = "statistic_clicked"
const val CALLBACK_DATA_TO_MENU = "to_menu_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

const val ALL_THE_WORDS_ARE_LEARNED = "Вы выучили все слова"