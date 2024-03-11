package com.example.bp_2324_v4.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bp_2324_v4.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.progressBar.visibility = View.VISIBLE

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val ref = firestore.collection("users").document(userId)
            ref.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name") ?: "N/A"
                    val email = documentSnapshot.getString("email") ?: "N/A"
                    val imageUrl = documentSnapshot.getString("imageUrl")

                    val pts = documentSnapshot.getLong("points")?.toInt() ?: 0
                    val level = documentSnapshot.getLong("lessons")?.toInt() ?: 0
                    val words = documentSnapshot.getLong("words")?.toInt() ?: 0

                    binding.tfFullName.text = name
                    binding.tfEmail.text = email

                    binding.tfPoints.text = pts.toString()
                    binding.tfLevels.text = level.toString()
                    binding.tfWords.text = words.toString()

                    // Kontrola, zda existuje URL obrázku, a načtení obrázku
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .into(binding.userFoto)
                    } else {
                        Toast.makeText(context, "No profile image found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Document does not exist", Toast.LENGTH_SHORT)
                        .show()
                }
                binding.progressBar.visibility = View.GONE
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Failed to get user data: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
            }
        }
        return binding.root
    }
}