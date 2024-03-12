import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName("ok")
    val ok: Boolean,
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
    val chatId: Long? = message?.chat?.id ?: callbackQuery?.message?.chat?.id
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("message")
    val message: Message,
    @SerialName("data")
    val data: String,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)