package com.example.bp_2324_v4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bp_2324_v4.recyclerOperations.LessonAdapter
import com.example.bp_2324_v4.databinding.FragmentPracticeBinding
import com.example.bp_2324_v4d.model.Lesson
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class PracticeFragment : Fragment() {

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)

        // Inicializujeme Firestore
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser?.uid

        val adapter = LessonAdapter(emptyList())

        binding.recyclerLessons.layoutManager = LinearLayoutManager(context)
        binding.recyclerLessons.adapter = adapter

        loadLessons()

        return binding.root
    }

    private fun loadLessons() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).collection("lessons")
            .get()
            .addOnSuccessListener { documents ->
                val lessonsList = documents.mapNotNull { document ->
                    val lessonNum = document.id.toInt()
                    val words = document.get("words") as? List<*>
                    val wordCount = words?.size ?: 0
                    val done = document.getBoolean("done") ?: false
                    Lesson(lessonNum, wordCount, done)
                }

                val adapter = LessonAdapter(lessonsList)
                binding.recyclerLessons.layoutManager = GridLayoutManager(context, 2)
                binding.recyclerLessons.adapter = adapter
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error loading lessons: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}


