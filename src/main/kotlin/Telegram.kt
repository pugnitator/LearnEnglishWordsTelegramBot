import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(3000)
        val botUpdates = getUpdates(botToken, updateId)
        println(botUpdates)

        val startIndex = botUpdates.lastIndexOf("update_id")
        val endIndex = botUpdates.lastIndexOf(",\n\"message")
        if (startIndex == -1 || endIndex == -1) continue

        val lastUpdateId = botUpdates.substring(startIndex + NUMBER_OF_CHARS_BEFORE_ID, endIndex)
        updateId = lastUpdateId.toInt() + 1
    }
}

fun getUpdates(botToken: String, updatesId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updatesId"
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}

const val NUMBER_OF_CHARS_BEFORE_ID = 11