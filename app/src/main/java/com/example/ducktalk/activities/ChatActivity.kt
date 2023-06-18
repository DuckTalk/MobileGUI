package com.example.ducktalk.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class ApiResponse(
    val error: Boolean,
    val message: String
)

interface ApiService {
    @POST("http://ableytner.ddns.net:2006")
    suspend fun sendMessage(@Body requestData: SendMessageRequestData): ApiResponse
}

class ChatActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize RecyclerView and adapter
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageAdapter = MessageAdapter()
        chatRecyclerView.adapter = messageAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ableytner.ddns.net:2006") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an ApiService instance
        apiService = retrofit.create(ApiService::class.java)

        // Example: Sending a message
        val messageText = "Hello, world!"
        sendMessage(messageText, isSent = true)
    }

    private fun sendMessage(messageText: String, isSent: Boolean) {
        // Show progress bar or perform other necessary actions

        val senderId = 225 // Replace with the actual sender ID
        val receiverData = ReceiverData("user", 92) // Replace with the actual receiver data
        val requestData = SendMessageRequestData(senderId, receiverData, messageText)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.sendMessage(requestData)
                if (!response.error) {
                    // Message sent successfully, display it
                    displayMessage(messageText, isSent)
                } else {
                    // Handle error case
                    Toast.makeText(this@ChatActivity, "Failed to send message", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle network or API error
                Toast.makeText(this@ChatActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                // Hide progress bar or perform other necessary actions
            }
        }
    }

    private fun displayMessage(messageText: String, isSent: Boolean) {
        // Add your logic to display the sent/received message
        // Create a Message object, update the adapter, etc.

        // Example:
        val message = Message(messageText, isSent)
        messageAdapter.addMessage(message)
        chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }
}

data class ReceiverData(
    val type: String,
    val user_id: Int
)

data class SendMessageRequestData(
    val sender_id: Int,
    val receiver: ReceiverData,
    val content: String
)

data class Message(
    val content: String,
    val isSent: Boolean
)

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messageList: MutableList<Message> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_container_sent_message, parent, false)
                SentMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_container_received_message, parent, false)
                ReceivedMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_SENT -> {
                val sentMessageViewHolder = holder as SentMessageViewHolder
                sentMessageViewHolder.bind(message)
            }
            VIEW_TYPE_RECEIVED -> {
                val receivedMessageViewHolder = holder as ReceivedMessageViewHolder
                receivedMessageViewHolder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        // Determine the view type based on the message
        val message = messageList[position]
        return if (message.isSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    fun addMessage(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val dateTimeTextView: TextView = itemView.findViewById(R.id.textDateTime)

        fun bind(message: Message) {
            messageTextView.text = message.content
            dateTimeTextView.text = message.content
            // Set other views in the sent message layout if needed
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val dateTimeTextView: TextView = itemView.findViewById(R.id.textDateTime)

        fun bind(message: Message) {
            messageTextView.text = message.content
            dateTimeTextView.text = message.content
            // Set other views in the received message layout if needed
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
}
