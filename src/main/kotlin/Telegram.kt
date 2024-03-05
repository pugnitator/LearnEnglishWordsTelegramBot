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
        Thread.sleep(2000)
        val botUpdates = telegramBot.getUpdates(updateId)
        println(botUpdates)

        val lastUpdateId: Int = getRegexValue(updateIdRegex, botUpdates)?.toIntOrNull()?: continue
        val chatId: String = getRegexValue(chatIdRegex, botUpdates) ?: continue
        val messageFromChat: String? = getRegexValue(messageRegex, botUpdates)
        val buttonCallbackData: String? = getRegexValue(buttonCallbackDataRegex, botUpdates)

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
                val answer = buttonCallbackData.substringAfter("_").toIntOrNull()?: continue
                telegramBot.checkNextQuestionAnswer(trainer, chatId, answer)
            }
        }

        updateId = lastUpdateId + 1
    }
}

fun getRegexValue(regexPattern: Regex, data: String): String? = regexPattern.find(data)?.groups?.get(1)?.value


const val BOT_COMMAND_START = "/start"

const val CALLBACK_DATA_LEARN_WORD = "learn_words_clicked"
const val CALLBACK_DATA_STATISTIC = "statistic_clicked"
const val CALLBACK_DATA_TO_MENU = "to_menu_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

const val ALL_THE_WORDS_ARE_LEARNED = "Вы выучили все слова"