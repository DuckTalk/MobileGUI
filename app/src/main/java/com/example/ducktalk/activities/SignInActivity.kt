package com.example.ducktalk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var buttonSignIn: MaterialButton
    private lateinit var textCreateNewAccount: TextView
    private val serverUrl = "http://ableytner.ddns.net:2007"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        textCreateNewAccount = findViewById(R.id.textCreateNewAccount)

        buttonSignIn.setOnClickListener {
            // Get the email and password entered by the user
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            // Check if the email and password are valid
            if (isValidEmail(email) && isValidPassword(password)) {
                // If the email and password are valid, check if they match the stored user credentials on the server
                val url = "$serverUrl/login"
                val jsonBody = JSONObject()
                jsonBody.put("email", email)
                jsonBody.put("password", password)

                val request = JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    { response ->
                        try {
                            val success = response.getBoolean("success")
                            if (success) {
                                // If the user credentials are valid, show a toast message indicating successful login
                                Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                                // You can also start a new activity here to go to the user's home screen
                            } else {
                                // If the user credentials are not valid, show an error message
                                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    {
                        Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
                    })

                // Add the request to the RequestQueue.
                Volley.newRequestQueue(this).add(request)
            } else {
                // If the email or password is invalid, show an error message
                Toast.makeText(this, "Please enter a valid email and password", Toast.LENGTH_SHORT).show()
            }
        }

        textCreateNewAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // Helper functions for checking email and password validity
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        // Password validation logic here, for example: check if password is at least 8 characters long
        return password.length >= 8
    }

    override fun onResume() {
        super.onResume()
        inputEmail.setText("")
        inputPassword.setText("")
    }
}
