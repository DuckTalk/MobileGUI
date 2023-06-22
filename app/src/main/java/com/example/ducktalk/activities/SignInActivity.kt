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
import com.example.ducktalk.utilities.Constants
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var buttonSignIn: MaterialButton
    private lateinit var textCreateNewAccount: TextView
    private val serverUrl = "http://ableytner.ddns.net:2006"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        textCreateNewAccount = findViewById(R.id.textCreateNewAccount)

        buttonSignIn.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (isValidEmail(email) && isValidPassword(password)) {
                val url = "$serverUrl/login"
                val jsonBody = JSONObject()
                jsonBody.put(Constants.KEY_EMAIL, email)
                jsonBody.put(Constants.KEY_PASSWORD, password)

                val request = JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    { response ->
                        try {
                            val success = response.getBoolean("success")
                            if (success) {
                                Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    {
                        Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
                    })

                Volley.newRequestQueue(this).add(request)
            } else {
                Toast.makeText(this, "Please enter a valid email and password", Toast.LENGTH_SHORT).show()
            }
        }

        textCreateNewAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    override fun onResume() {
        super.onResume()
        inputEmail.setText("")
        inputPassword.setText("")
    }
}
