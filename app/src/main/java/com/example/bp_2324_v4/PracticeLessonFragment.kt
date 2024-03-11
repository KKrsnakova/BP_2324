package com.example.bp_2324_v4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.databinding.FragmentPracticeLessonBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class PracticeLessonFragment : Fragment() {
    private var _binding: FragmentPracticeLessonBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUserId: String
    private var wordCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPracticeLessonBinding.inflate(inflater, container, false)


        // Inicializujeme Firestore
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser?.uid.toString()



        val lessonNum = arguments?.getString("lessonNum")
        if (lessonNum != null) {
            val wordCountRef = firestore.collection("users").document(currentUserId)
                .collection("lessons").document(lessonNum)

            wordCountRef.get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val wordCounts = doc.getLong("wordCount") ?: 0
                    wordCount = wordCounts.toInt()
                    binding.tvWordCount.text = "Words count: $wordCount"
                    binding.tvLessonNum.text = "Lesson number: $lessonNum"
                } else {
                    Toast.makeText(context, "Lesson not found.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching lesson data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Lesson number is not provided.", Toast.LENGTH_SHORT).show()
        }

        binding.btnStart.setOnClickListener {
            val fragmentQuestion = WordQuestionFragment()

            // Příprava argumentů
            val args = Bundle()
            args.putString("lessonNum", lessonNum) // Přidání čísla lekce
            fragmentQuestion.arguments = args

            // Otevření nového fragmentu
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, fragmentQuestion)
                addToBackStack(null)
                commit()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Uvolnění reference na binding
    }


}