import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBot = TelegramBot(botToken)

    // список ЧатИд - Трейнер
    val trainers = HashMap<Long, LearnWordsTrainer>()
//    val trainer = LearnWordsTrainer()
    var lastUpdateId = 0L

    val json = Json { ignoreUnknownKeys = true }

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramBot.getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString<Response>(responseString)
        val updates = response.result
        if (updates.isEmpty()) continue

        val sortedUpdates = updates.sortedBy { it.updateId }

        val updatesForEachChatId: Map<Long, List<Update>> =
            sortedUpdates.filter { it.chatId != null }.groupBy { it.chatId!! }
        telegramBot.handleUpdate(updatesForEachChatId, trainers)

        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}