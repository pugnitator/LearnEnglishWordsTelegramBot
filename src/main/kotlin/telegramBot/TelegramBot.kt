package telegramBot

import trainer.LearnWordsTrainer
import trainer.Question
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
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
    private val json = Json { ignoreUnknownKeys = true }

    fun getUpdates(updatesId: Long): Response {
        val urlGetUpdates = "$botURL$botToken/getUpdates?offset=$updatesId"
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        println(responseString)

        val response: Response = json.decodeFromString<Response>(responseString)
        return response
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

        val learnWordButton = InlineKeyboardButton(BUTTON_LEARN_WORD, CALLBACK_DATA_LEARN_WORD)
        val statisticsButton = InlineKeyboardButton(BUTTON_STATISTIC, CALLBACK_DATA_STATISTIC)
        val resetStatisticButton = InlineKeyboardButton(BUTTON_RESET_STATISTIC, CALLBACK_DATA_RESET_STATISTIC)

        val replyMarkup = ReplyMarkup(listOf(listOf(learnWordButton), listOf(statisticsButton, resetStatisticButton)))
        val menuBody = json.encodeToJsonElement(SendMessageRequest(chatId, "Меню", replyMarkup)).toString()

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(menuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    private fun sendQuestion(chatId: Long, currentQuestion: Question): String {
        val urlSendMessage = "$botURL$botToken/sendMessage"

        val toMenuButton = InlineKeyboardButton(BUTTON_TO_MENU, CALLBACK_DATA_TO_MENU)
        val answerOptionBody = currentQuestion.answerOptions.mapIndexed { index, word ->
            listOf(InlineKeyboardButton("${index + 1}. ${word.translation}", "$CALLBACK_DATA_ANSWER_PREFIX$index"))
        }.toMutableList()

        answerOptionBody.add(listOf(toMenuButton))

        val replyMarkup = ReplyMarkup(answerOptionBody)
        val questionBody = json.encodeToJsonElement(
            SendMessageRequest(
                chatId,
                "Выберите правильный перевод для слова \"${currentQuestion.wordToStudy.original}\"",
                replyMarkup
            )
        ).toString()

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(questionBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun getNextQuestion(trainer: LearnWordsTrainer, chatId: Long) {
        val currentQuestion = trainer.getNextQuestion()
        if (currentQuestion == null) {
            sendMessage(chatId, ALL_THE_WORDS_ARE_LEARNED)
            sendMenu(chatId)
        } else sendQuestion(chatId, currentQuestion)
    }

    fun checkNextQuestionAnswer(trainer: LearnWordsTrainer, chatId: Long, answer: Int) {
        if (trainer.isAnswerCorrect(answer)) sendMessage(chatId, "Правильно!")
        else sendMessage(
            chatId,
            "Неверно. Правильный ответ ${trainer.currentQuestion?.wordToStudy?.translation ?: "не обнаружен"}"
        )
        getNextQuestion(trainer, chatId)
    }
}