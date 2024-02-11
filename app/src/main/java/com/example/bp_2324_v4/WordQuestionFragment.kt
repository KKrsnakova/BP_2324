package com.example.bp_2324_v4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bp_2324_v4.databinding.FragmentWordQuestionBinding
import com.example.bp_2324_v4.fragments.PracticeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class WordQuestionFragment : Fragment() {
    private var _binding: FragmentWordQuestionBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var lessonNum: String
    private lateinit var wordsList: List<Map<String, String>>
    private var currentWordIndex: Int = 0

    private var points: Int = 0
    private var skipped: Int = 0
    private var mistakes: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWordQuestionBinding.inflate(inflater, container, false)

        // Inicializace Firestore a FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser?.uid.toString()

        // Načtení čísla lekce z argumentů
        arguments?.let {
            lessonNum = it.getString("lessonNum") ?: ""
        }

        // Spuštění procvičování
        startPractice()

        binding.tvLessonNumber.text = "Lesson $lessonNum"

        binding.btnSkip.setOnClickListener {
            skipped++
            if (currentWordIndex < wordsList.size - 1) {
                currentWordIndex++
                displayWord(currentWordIndex)

            } else {
                // Dosáhli jsme konce slovíček, zobrazíme zprávu o dokončení
                updateLessonStats()
                Toast.makeText(context, "You have completed all words!", Toast.LENGTH_SHORT).show()
                showFinishStats()
            }
        }

        binding.btnClose.setOnClickListener {
            val fragmentPractice = PracticeFragment()
            // Otevření nového fragmentu
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, fragmentPractice)
                addToBackStack(null)
                commit()
            }
        }



        return binding.root
    }

    private fun showFinishStats() {
        val fragmentFinish = FinishFragment()

        // Příprava argumentů
        val args = Bundle()
        args.putString("lessonNum", lessonNum) // Přidání čísla lekce
        fragmentFinish.arguments = args

        // Otevření nového fragmentu
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragmentFinish)
            addToBackStack(null)
            commit()

        }
    }

    private fun updateLessonStats() {
        val lessonRef = firestore.collection("users").document(currentUserId)
            .collection("lessons").document(lessonNum)

        // Vytvoření mapy s novými hodnotami statistik
        val newStats: Map<String, Any> = hashMapOf(
            "points" to points,
            "skipped" to skipped,
            "mistakes" to mistakes
        )

        // Aktualizace fields v dokumentu
        lessonRef.update(newStats)
            .addOnSuccessListener {
                Toast.makeText(context, "Lesson statistics updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error updating lesson statistics: $e", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun startPractice() {
        // Načtení slov z databáze pro danou lekci
        firestore.collection("users").document(currentUserId).collection("lessons")
            .document(lessonNum)
            .get()
            .addOnSuccessListener { document ->
                wordsList = document.get("words") as? List<Map<String, String>> ?: emptyList()
                if (wordsList.isNotEmpty()) {
                    // Zobrazení prvního slova
                    displayWord(currentWordIndex)

                    binding.btnCheck.setOnClickListener {
                        checkAnswer()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "No words found in lesson: $lessonNum",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error fetching words for lesson $lessonNum: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun displayWord(index: Int) {
        binding.btnNext.visibility = View.GONE
        val word = wordsList[index]
        val czechWord = word["czech"]
        val englishWord = word["english"]
        binding.tvCzechWord.text = czechWord
        binding.etEnglishWord.setText("")
    }

    private fun checkAnswer() {
        // Získání odpovědi od uživatele
        val userInput = binding.etEnglishWord.text.toString().trim()
        // Získání anglického slovíčka k porovnání
        val englishWord = wordsList[currentWordIndex]["english"] ?: ""
        // Porovnání zadané odpovědi s anglickým slovíčkem
        if (userInput.equals(englishWord, ignoreCase = true)) {
            // Správná odpověď
            binding.etEnglishWord.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.textGreen
                )
            )
            Toast.makeText(context, "Correct answer!", Toast.LENGTH_SHORT).show()
            points += 10 // Přidání bodů za správnou odpověď
            binding.btnNext.visibility = View.VISIBLE
            binding.btnCheck.visibility = View.GONE
        } else {
            // Špatná odpověď
            binding.etEnglishWord.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.textRed)
            )
            Toast.makeText(context, "Wrong answer, try again!", Toast.LENGTH_SHORT).show()
            mistakes++ // Zvýšení počtu chyb
        }

        binding.btnNext.setOnClickListener {
            binding.btnCheck.visibility = View.VISIBLE
            binding.etEnglishWord.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.textBlack)
            )
            if (currentWordIndex < wordsList.size - 1) {
                currentWordIndex++
                displayWord(currentWordIndex)
            } else {
                // Dosáhli jsme konce slovíček, zobrazíme zprávu o dokončení
                updateLessonStats()
                Toast.makeText(context, "You have completed all words!", Toast.LENGTH_SHORT).show()
                showFinishStats()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}