import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBot = TelegramBot(botToken)

    val trainers = HashMap<Long, LearnWordsTrainer>()

    var lastUpdateId = 0L

    val json = Json { ignoreUnknownKeys = true }

    while (true) {
        Thread.sleep(800)
        val responseString = telegramBot.getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString<Response>(responseString)

        if (!response.ok) {
            println("Ошибка получения апдейта. Программа завершается :(")
            break
        } else {
            val updates = response.result
            if (updates.isEmpty()) continue

            val sortedUpdates = updates.sortedBy { it.updateId }

            val updatesForEachChatId: Map<Long, List<Update>> =
                sortedUpdates.filter { it.chatId != null }.groupBy { it.chatId!! }
            telegramBot.handleUpdate(updatesForEachChatId, trainers)

            lastUpdateId = sortedUpdates.last().updateId + 1
        }
    }
}