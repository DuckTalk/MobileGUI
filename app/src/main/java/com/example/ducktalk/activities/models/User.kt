package com.example.ducktalk.activities.models

import java.io.Serializable

data class User(var name: String = "", var image: String = "", var email: String = "", var token: String = ""): Serializable
