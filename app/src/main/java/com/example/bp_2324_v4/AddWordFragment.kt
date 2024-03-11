package com.example.bp_2324_v4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.databinding.FragmentAddWordBinding
import com.example.bp_2324_v4.fragments.DictionaryFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class AddWordFragment : Fragment() {
    private var _binding: FragmentAddWordBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWordBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // Získání nejvyššího čísla lekce
        getHighestLessonNumber()

        binding.btnSaveClose.setOnClickListener {
            val cz = binding.tfczWord.text.toString().trim()
            val en = binding.tfenWord.text.toString().trim()
            val lessonNum = binding.tfLessonNum.text.toString().trim().toIntOrNull() ?: 0

            if (cz.isNotEmpty() && en.isNotEmpty()) {
                addWordToLesson(cz, en, lessonNum)

                // Návrat do DictionaryFragment
                backToDictionary()

            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnSaveAddNext.setOnClickListener {


            val cz = binding.tfczWord.text.toString().trim()
            val en = binding.tfenWord.text.toString().trim()
            val lessonNum = binding.tfLessonNum.text.toString().trim().toIntOrNull() ?: 0

            if (cz.isNotEmpty() && en.isNotEmpty()) {
                addWordToLesson(cz, en, lessonNum)
            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.saveClose.setOnClickListener {
            backToDictionary()
        }
        return binding.root
    }

    private fun getHighestLessonNumber() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val lessonsRef = firestore.collection("users").document(userId)
                .collection("lessons")

            lessonsRef
                .get()
                .addOnSuccessListener { documents ->
                    var highestLessonNumber = 0

                    for (document in documents) {
                        val lessonNumString = document.id
                        val lessonNum = lessonNumString.toIntOrNull() ?: 0
                        if (lessonNum > highestLessonNumber) {
                            highestLessonNumber = lessonNum
                        }
                    }

                    binding.tfLessonNum.setText(highestLessonNumber.toString())
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Error: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun addWordToLesson(czechWord: String, englishWord: String, lessonNum: Int) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val lessonRef = firestore.collection("users").document(userId)
                .collection("lessons").document(lessonNum.toString())

            lessonRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    updateWordInLesson(lessonRef, czechWord, englishWord)
                } else {
                    createLessonWithWord(lessonRef, czechWord, englishWord)
                }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun backToDictionary() {
        val backToDictionary = DictionaryFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, backToDictionary).addToBackStack(null).commit()
    }

    private fun updateWordInLesson(
        lessonRef: DocumentReference,
        czechWord: String,
        englishWord: String
    ) {
        lessonRef.update(
            "words",
            FieldValue.arrayUnion(hashMapOf("czech" to czechWord, "english" to englishWord))
        ).addOnSuccessListener {
            // Inkrementace wordCount
            lessonRef.update("wordCount", FieldValue.increment(1)).addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Word added to lesson successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Error adding word to lesson: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createLessonWithWord(
        lessonRef: DocumentReference,
        czechWord: String,
        englishWord: String
    ) {
        val newLessonData = hashMapOf(
            "words" to listOf(hashMapOf("czech" to czechWord, "english" to englishWord)),
            "wordCount" to 1,
            "points" to 0,
            "skipped" to 0,
            "mistakes" to 0,
            "done" to false
        )

        lessonRef.set(newLessonData)
            .addOnSuccessListener {
            Toast.makeText(requireContext(), "New lesson created with word", Toast.LENGTH_SHORT)
                .show()

           }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Error creating new lesson: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
