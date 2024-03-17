package com.example.bp_2324_v4.practice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.databinding.FragmentFinishBinding
import com.example.bp_2324_v4.fragments.PracticeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class FinishFragment : Fragment() {

    private var _binding: FragmentFinishBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var lessonNum: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFinishBinding.inflate(inflater, container, false)



        // Inicializace Firestore a FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser?.uid.toString()

        // Načtení čísla lekce z argumentů
        arguments?.let {
            lessonNum = it.getString("lessonNum") ?: ""
        }

         binding.tvLessonNumber.text = "Lesson $lessonNum"

        binding.btnOK.setOnClickListener {
            val backToPracticeMain = PracticeFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, backToPracticeMain).addToBackStack(null).commit()
        }



        val lessonStatsRef = firestore.collection("users").document(currentUserId)
            .collection("lessons").document(lessonNum)

        lessonStatsRef.get().addOnSuccessListener { stats ->
            if (stats.exists()) {
                val points = stats.getLong("points") ?: 0
                val skipped = stats.getLong("skipped") ?: 0
                val mistakes = stats.getLong("mistakes") ?: 0
                val wordCount = stats.getLong("wordCount") ?: 0
                lessonStatsRef.update("done", true)

                // Převod čísel
                binding.tvPoints.text = "$points"
                binding.tvSkipped.text = "$skipped/$wordCount"
                binding.tvMistakes.text = "$mistakes"

               val ref = firestore.collection("users").document(currentUserId)

                ref.update("points", FieldValue.increment(points))

            } else {
                Toast.makeText(context, "Lesson not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Error fetching lesson data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }
 }