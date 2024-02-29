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
        val encodedText = URLEncoder.encode(text,StandardCharsets.UTF_8)
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
            					"callback_data": "learn_words_clicked"
            				}
            			],
            			[
            				{
            					"text": "Статистика",
            					"callback_data": "statistic_clicked"
            				},
            				{
            					"text": "Выход",
            					"callback_data": "stop_bot"
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
}