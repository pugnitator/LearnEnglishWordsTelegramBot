import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBot(
    private val botToken: String,
) {
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updatesId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updatesId"
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: String, text: String): String {
        val encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$encodedText"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(chatId: String): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
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

    fun sendQuestion(chatId: String, currentQuestion: Question?): String {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"

        val questionBody = """
            {
            	"chat_id": $chatId,
                "text": "Выберите правильный перевод для слова \"${currentQuestion!!.wordToStudy.original}\"",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{                           
            					"text": "1. ${currentQuestion.answerOptions.elementAt(0).translation}",
            					"callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}0"
            				}
            			],
            			[
            				{
            					"text": "2. ${currentQuestion.answerOptions.elementAt(1).translation}",
            					"callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}1"
            				}
            			],
            			[
            				{
            					"text": "3. ${currentQuestion.answerOptions.elementAt(2).translation}",
            					"callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}2"
            				}
            			],
            			[
            				{
            					"text": "4. ${currentQuestion.answerOptions.elementAt(3).translation}",
            					"callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}3"
            				}
            			],
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

    fun checkNextQuestionAnswer(trainer: LearningWordsTrainer, chatId: String, answer: Int?) {
        if (trainer.isAnswerCorrect(answer)) sendMessage(chatId, "Правильно!")
        else sendMessage(chatId, "Неверно.Правильный ответ ${trainer.currentQuestion?.wordToStudy?.translation}")

        if (trainer.getNextQuestion() == null) sendMessage(chatId, ALL_THE_WORDS_ARE_LEARNED)

        sendQuestion(chatId, trainer.getNextQuestion())
    }
}