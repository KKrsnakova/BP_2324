package com.example.bp_2324_v4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bp_2324_v4.databinding.FragmentAddWordBinding
import com.example.bp_2324_v4.fragments.DictionaryFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID


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

        binding.saveButton.setOnClickListener {
            val cz = binding.tfczWord.text.toString().trim()
            val en = binding.tfenWord.text.toString().trim()
            val lessonNum = binding.tfLessonNum.text.toString().trim()

            if (cz.isNotEmpty() && en.isNotEmpty()) {
                addWordToLesson(cz, en, lessonNum)



            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.saveClose.setOnClickListener {
            val backToDictionary = DictionaryFragment()
            val transaction = parentFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment_container, backToDictionary)
            transaction.addToBackStack(null)

            transaction.commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addWordToLesson(czechWord: String, englishWord: String, lessonNum: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val lessonRef = firestore.collection("users").document(userId)
                .collection("lessons").document(lessonNum)

            lessonRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    updateWordInLesson(lessonRef, czechWord, englishWord)
                } else {
                    createLessonWithWord(lessonRef, czechWord, englishWord)
                }
                val backToDictionary = DictionaryFragment()
                val transaction = parentFragmentManager.beginTransaction()

                transaction.replace(R.id.fragment_container, backToDictionary)
                transaction.addToBackStack(null)

                transaction.commit()

            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateWordInLesson(lessonRef: DocumentReference, czechWord: String, englishWord: String) {
        lessonRef.update("words", FieldValue.arrayUnion(hashMapOf("czech" to czechWord, "english" to englishWord)))
            .addOnSuccessListener {
                // Inkrementace wordCount
                lessonRef.update("wordCount", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Word added to lesson successfully", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error adding word to lesson: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun createLessonWithWord(lessonRef: DocumentReference, czechWord: String, englishWord: String) {
        val newLessonData = hashMapOf(
            "words" to listOf(hashMapOf("czech" to czechWord, "english" to englishWord)),
            "wordCount" to 1, // Nastavení počátečního počtu slov
            "points" to 0,
            "skipped" to 0,
            "mistakes" to 0


        )

        lessonRef.set(newLessonData).addOnSuccessListener {
            Toast.makeText(requireContext(), "New lesson created with word", Toast.LENGTH_SHORT).show()

            // Návrat do DictionaryFragment
            val backToDictionary = DictionaryFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, backToDictionary).addToBackStack(null).commit()
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error creating new lesson: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
