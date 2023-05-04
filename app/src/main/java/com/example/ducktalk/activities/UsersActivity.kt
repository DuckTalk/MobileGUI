package com.example.ducktalk.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ducktalk.activities.databinding.ActivityUsersBinding
import com.example.ducktalk.adapters.UsersAdapter
import com.example.ducktalk.models.User
import com.example.ducktalk.utilities.Constants
import com.example.ducktalk.utilities.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONArray
import java.io.IOException

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
        getUsers()
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener { onBackPressed() }
    }

    private fun getUsers() {
        loading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = okhttp3.Request.Builder()
                    .url("http://ableytner.ddns.net:2006")
                    .build()
                val response = client.newCall(request).execute()
                val body = response.body?.string()
                if (body != null) {
                    val users = parseUsers(body)
                    withContext(Dispatchers.Main) {
                        showUsers(users)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showErrorMessage()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    showErrorMessage()
                }
            }
            loading(false)
        }
    }

    private fun parseUsers(responseBody: String): List<User> {
        val users: MutableList<User> = ArrayList()
        val jsonArray = JSONArray(responseBody)
        val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (currentUserId == jsonObject.getString("id")) {
                continue
            }
            val user = User()
            user.name = jsonObject.getString(Constants.KEY_NAME)
            user.email = jsonObject.getString(Constants.KEY_EMAIL)
            user.image = jsonObject.getString(Constants.KEY_IMAGE)
            user.token = jsonObject.getString(Constants.KEY_FCM_TOKEN)
            users.add(user)
        }
        return users
    }

    private fun showUsers(users: List<User>) {
        if (users.isNotEmpty()) {
            usersAdapter = UsersAdapter(users)
            binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.usersRecyclerView.adapter = usersAdapter
            binding.usersRecyclerView.visibility = View.VISIBLE
        } else {
            showErrorMessage()
        }
    }

    private fun showErrorMessage() {
        binding.textErrorMessage.text = String.format("%s", "No user available")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}
