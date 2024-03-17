package com.example.bp_2324_v4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

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


                    val stars = pts / 500

                    binding.apply {
                        tfFullName.text = name
                        tfEmail.text = email

                        tfLessons.text = level.toString()
                        tfLevels.text = stars.toString()
                        tfWords.text = words.toString()

                        val progress = (stars+1)*500
                        pointsPB.max = progress
                        pointsPB.progress = pts

                        progressText.text = "$pts/$progress"
                    }


                    // Dynamicky přidat hvězdy
                    addStars(stars)

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

    // Metoda pro dynamické přidání hvězd do layoutu
    private fun addStars(starCount: Int) {
        val starContainer = binding.starContainer
        starContainer.removeAllViews() // Odstraní existující hvězdy

        val starImages = listOf(
            R.drawable.h1bg,
            R.drawable.h2bg,
            R.drawable.h3bg,
            R.drawable.h4bg,
            R.drawable.h5bg,
            R.drawable.h6bg,
            R.drawable.h7bg,
            R.drawable.h8bg,
            R.drawable.h9bg
        )


        repeat(starCount) {index ->
            val starLayout = RelativeLayout(requireContext())
            val starImageView = ImageView(requireContext())
            val starTextView = TextView(requireContext())

            val layoutParams = ViewGroup.LayoutParams(230, 230)
            starImageView.layoutParams = layoutParams

            if (index < starImages.size) {
                val imageResource = starImages[index]
                starImageView.setImageResource(imageResource)
                starLayout.addView(starImageView)
            } else {
                starImageView.setImageResource(R.drawable.star)
                starLayout.addView(starImageView)
                starTextView.text = (index + 1).toString()
                starLayout.addView(starTextView)

            }



            val textParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            starTextView.layoutParams = textParams

            starContainer.addView(starLayout)
        }
    }
}