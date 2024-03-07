import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBot(
    private val botToken: String,
) {
    private val botURL: String = "https://api.telegram.org/bot"
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updatesId: Long): String {
        val urlGetUpdates = "$botURL$botToken/getUpdates?offset=$updatesId"
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: Long, text: String): String {
        val encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val urlSendMessage = "$botURL$botToken/sendMessage?chat_id=$chatId&text=$encodedText"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(chatId: Long): String {
        val urlSendMessage = "$botURL$botToken/sendMessage"
        val menuBody = """
            {
            	"chat_id": $chatId,
            	"text": "Меню",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{
            					"text": "Учить слова",
            					"callback_data": "$CALLBACK_DATA_LEARN_WORD"
            				},
                            {
                                "text": "Статистика",
                                "callback_data": "$CALLBACK_DATA_STATISTIC"
                            }
            			]
            		]
            	}
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(menuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    private fun sendQuestion(chatId: Long, currentQuestion: Question): String {
        val urlSendMessage = "$botURL$botToken/sendMessage"
        val answerOptionBody = currentQuestion.answerOptions.mapIndexed { index, word ->
            """
            [
                {
                    "text": "${index + 1}. ${word.translation}",
                    "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}$index"
                }
            ]   
            """.trimIndent()
        }.joinToString(",")

        val questionBody = """
            {
            	"chat_id": $chatId,
                "text": "Выберите правильный перевод для слова \"${currentQuestion.wordToStudy.original}\"",
            	"reply_markup": {
            		"inline_keyboard": [
                        $answerOptionBody,
            			[
            				{
            					"text": "В меню",
            					"callback_data": "$CALLBACK_DATA_TO_MENU"
            				}
            			]
            		]
            	}
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(questionBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun getNextQuestion(trainer: LearningWordsTrainer, chatId: Long){
        val currentQuestion = trainer.getNextQuestion()
        if (currentQuestion == null) {
            sendMessage(chatId, ALL_THE_WORDS_ARE_LEARNED)
            sendMenu(chatId)
        } else sendQuestion(chatId, currentQuestion)
    }

    fun checkNextQuestionAnswer(trainer: LearningWordsTrainer, chatId: Long, answer: Int) {
        if (trainer.isAnswerCorrect(answer)) sendMessage(chatId, "Правильно!")
        else sendMessage(chatId, "Неверно. Правильный ответ ${trainer.currentQuestion?.wordToStudy?.translation?: "не обнаружен"}")

        getNextQuestion(trainer, chatId)
    }
}