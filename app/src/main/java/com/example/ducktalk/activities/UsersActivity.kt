package com.example.ducktalk.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class UserActivity : AppCompatActivity() {

    private val TAG = "UserActivity"
    private val baseUrl = "http://ableytner.ddns.net:2006"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        // Call functions here
    }

    private fun createNewUser(username: String, email: String, password: String) {
        GlobalScope.launch {
            val client = OkHttpClient()
            val url = "$baseUrl/api/user"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = JSONObject().apply {
                put("username", username)
                put("email", email)
                put("pw_hash", password)
            }.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    Log.d(TAG, responseBody)
                }
            }
        }
    }

    private fun getUserById(userId: Int) {
        GlobalScope.launch {
            val client = OkHttpClient()
            val url = "$baseUrl/api/user/$userId"
            val request = Request.Builder()
                .url(url)
                .build()
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    Log.d(TAG, responseBody)
                }
            }
        }
    }

    private fun sendMessage(senderId: Int, receiverType: String, receiverId: Int, content: String) {
        GlobalScope.launch {
            val client = OkHttpClient()
            val url = "$baseUrl/api/message"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = JSONObject().apply {
                put("sender_id", senderId)
                put("receiver", JSONObject().apply {
                    put("type", receiverType)
                    put("user_id", receiverId)
                })
                put("content", content)
            }.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    Log.d(TAG, responseBody)
                }
            }
        }
    }

    private fun getSalt(email: String) {
        GlobalScope.launch {
            val client = OkHttpClient()
            val url = "$baseUrl/api/salt?email=$email"
            val request = Request.Builder()
                .url(url)
                .build()
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    Log.d(TAG, responseBody)
                }
            }
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("DuckTalkPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }


    private fun requestToken(email: String, password: String) {
        GlobalScope.launch {
            val client = OkHttpClient()
            val url = "$baseUrl/api/token"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = JSONObject().apply {
                put("email", email)
                put("pw_hash", password)
            }.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            try {
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                val jsonObject = JSONObject(responseData)
                val token = jsonObject.getString("token")
                saveToken(token)
            } catch (e: IOException) {
// handle network or server errors here
            } catch (e: JSONException) {
// handle JSON parsing errors here
            }
        }
    }

    private fun deleteToken() {
        val sharedPreferences = getSharedPreferences("DuckTalkPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.apply()
    }
}


