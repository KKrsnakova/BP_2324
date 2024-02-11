package com.example.bp_2324_v4

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bp_2324_v4.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var imageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.imgAdd.setImageURI(it)
                imageUri = it
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnAddFoto.setOnClickListener {
            pickImage.launch("image/*")  // Spustí intent pro výběr obrázku
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.tfEmail.text.toString().trim()
            val password = binding.tfPassword.text.toString().trim()
            val passwordCheck = binding.tfPasswordCheck.text.toString().trim()
            val name = binding.tfFullName.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && passwordCheck.isNotEmpty() && name.isNotEmpty()) {
                if (password == passwordCheck) {
                    if (imageUri != null) {
                        // Pokračovat v registraci a nahrání obrázku
                        registerUser(email, password, name, imageUri!!, 2)
                    } else {
                        Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tfAlreadyReg.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        name: String,
        imageUri: Uri,
        role: Int
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    if (userId.isNotEmpty()) {
                        // Teď nahráváme obrázek, protože uživatel je úspěšně zaregistrován
                        uploadImageToStorageAndSaveUserInfo(userId, name, password, email, imageUri)
                    } else {
                        Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun uploadImageToStorageAndSaveUserInfo(
        userId: String,
        name: String,
        password: String,
        email: String,
        uri: Uri
    ) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    saveUserInfo(userId, name, password, email, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserInfo(
        userId: String,
        name: String,
        password: String,
        email: String,
        imageUrl: String
    ) {
        val userMap = hashMapOf(
            "name" to name,
            "password" to password,
            "email" to email,
            "imageUrl" to imageUrl,
            "role" to 2, //výchozí role User (2), Admin (1)
            "lessons" to 0,
            "points" to 0,
            "words" to 0
        )

        firestore.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LogInActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}