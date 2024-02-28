
fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBot = TelegramBot(botToken)
    var updateId = 0

    val updateIdRegex: Regex = "\"update_id\":(.+[0-9]),".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(.+?),".toRegex()
    val messageRegex: Regex = "\"text\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(3000)
        val botUpdates = telegramBot.getUpdates(updateId)
        println(botUpdates)

        val lastUpdateId: Int? = getRegexValue(updateIdRegex, botUpdates)?.toIntOrNull()
        val chatId: String? = getRegexValue(chatIdRegex, botUpdates)
        val messageFromChat: String? = getRegexValue(messageRegex, botUpdates)

        if (messageFromChat.equals("hello", ignoreCase = true) && chatId != null)
            telegramBot.sendMessage(chatId, "Hello!")

        updateId = (lastUpdateId ?: continue) + 1
    }
}

fun getRegexValue(regexPattern: Regex, data: String): String? = regexPattern.find(data)?.groups?.get(1)?.value