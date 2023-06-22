package com.example.ducktalk.activities

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ducktalk.activities.databinding.ActivityMainBinding
import com.example.ducktalk.utilities.Constants
import com.example.ducktalk.utilities.PreferenceManager
import org.json.JSONObject

class UsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        loadUserDetails()
        getToken()
        setListeners()
    }

    private fun setListeners() {
        binding.imageSignOut.setOnClickListener { signOut() }
        binding.fabNewChat.setOnClickListener { startActivity(Intent(applicationContext, UsersActivity::class.java)) }
    }

    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun getToken() {
        val url = "http://ableytner.ddns.net:2006${preferenceManager.getString(Constants.KEY_USER_ID)}"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val token = response.getString(Constants.KEY_FCM_TOKEN)
                updateToken(token)
            },
            { error ->
                showToast("Unable to get token")
                error.printStackTrace()
            })
        Volley.newRequestQueue(applicationContext).add(request)
    }

    private fun updateToken(token: String) {
        val url = "http://ableytner.ddns.net:2006"
        val requestBody = JSONObject()
        requestBody.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
        requestBody.put(Constants.KEY_FCM_TOKEN, token)
        val request = JsonObjectRequest(Request.Method.PUT, url, requestBody,
            {
                showToast("Token updated successfully")
            },
            { error ->
                showToast("Unable to update token")
                error.printStackTrace()
            })
        Volley.newRequestQueue(applicationContext).add(request)
    }

    private fun signOut() {
        showToast("Signing out...")
        val url = "http://ableytner.ddns.net:2006${preferenceManager.getString(Constants.KEY_USER_ID)}"
        val request = JsonObjectRequest(
            Request.Method.DELETE, url, null,
            {
                preferenceManager.clear()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            },
            { error ->
                showToast("Unable to sign out")
                error.printStackTrace()
            })
        Volley.newRequestQueue(applicationContext).add(request)
    }
}
