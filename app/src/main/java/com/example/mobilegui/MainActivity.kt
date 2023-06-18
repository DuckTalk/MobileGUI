package com.example.mobilegui

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var chatLayout: LinearLayout
    private lateinit var messageEditText: EditText
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()

        promptUsername()
    }

    private fun initializeViews() {
        chatLayout = findViewById(R.id.chatLayout)
        messageEditText = findViewById(R.id.messageEditText)

        val sendButton: Button = findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun promptUsername() {
        val inputLayout = LinearLayout(this)
        inputLayout.orientation = LinearLayout.VERTICAL

        val usernameEditText = EditText(this)
        usernameEditText.hint = "Benutzernamen eingeben"

        val usernameButton = Button(this)
        usernameButton.text = "Speichern"
        usernameButton.setOnClickListener {
            val inputUsername = usernameEditText.text.toString().trim()
            if (inputUsername.isNotEmpty()) {
                username = inputUsername
                setContentView(R.layout.activity_main)
                initializeViews()
            } else {
                Snackbar.make(chatLayout, "Bitte einen Benutzernamen eingeben", Snackbar.LENGTH_SHORT).show()
            }
        }

        inputLayout.addView(usernameEditText)
        inputLayout.addView(usernameButton)

        setContentView(inputLayout)
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()

        if (messageText.isNotEmpty()) {
            val messageView = layoutInflater.inflate(R.layout.message_item, null)
            val usernameTextView: TextView = messageView.findViewById(R.id.usernameTextView)
            val messageTextView: TextView = messageView.findViewById(R.id.messageTextView)
            val timestampTextView: TextView = messageView.findViewById(R.id.timestampTextView)

            usernameTextView.text = username
            messageTextView.text = messageText
            timestampTextView.text = getCurrentTimestamp()

            chatLayout.addView(messageView)
            messageEditText.text.clear()
        } else {
            Snackbar.make(chatLayout, "Bitte eine Nachricht eingeben", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentTimestamp(): String {
        val currentTime = System.currentTimeMillis()
        return DateFormat.format("HH:mm:ss", currentTime).toString()
    }
}
