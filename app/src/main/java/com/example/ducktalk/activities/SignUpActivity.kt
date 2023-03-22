package com.example.ducktalk.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ducktalk.databinding.ActivitySignUpBinding
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

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

    private fun signUp() {
        // Add your sign-up code here
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageData: Intent? = result.data
            imageData?.data?.let { imageUri ->
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                    val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility = View.GONE
                    encodedImage = bitmap?.let { encodeImage(it) }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
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

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignUp.visibility = View.VISIBLE
        }
    }
}
