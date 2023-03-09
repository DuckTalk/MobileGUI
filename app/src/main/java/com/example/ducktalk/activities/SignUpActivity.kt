package com.example.ducktalk.activities

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ducktalk.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var encodedImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener { onBackPressed() }
        binding.buttonSignUp.setOnClickListener {
            if (isValidSignUpDetails()) {
                signUp()

            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignUpDetails(): Boolean {
        return when {
            encodedImage == null -> {
                showToast("Select profile image")
                false
            }
            binding.inputName.text.toString().trim().isEmpty() -> {
                showToast("Enter name")
                false
            }
            binding.inputEmail.text.toString().trim().isEmpty() -> {
                showToast("Enter email")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches() -> {
                showToast("Enter valid email")
                false
            }
            binding.inputPassword.text.toString().trim().isEmpty() -> {
                showToast("Enter password")
                false
            }
            binding.inputConfirmPassword.text.toString().trim().isEmpty() -> {
                showToast("Confirm password")
                false
            }
            !binding.inputPassword.text.toString().equals(binding.inputConfirmPassword.text.toString()) -> {
                showToast("Password and confirm password must be the same")
                false
            }
            else -> true
        }
    }

    private fun signUp() {
        // Add your sign-up code here
    }
}
