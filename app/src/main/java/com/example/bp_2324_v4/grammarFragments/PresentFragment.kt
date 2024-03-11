package com.example.bp_2324_v4.grammarFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.databinding.FragmentPresentBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class PresentFragment : Fragment() {

    private var _binding: FragmentPresentBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestoreDb: FirebaseFirestore
    private var isEnglish = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        firestoreDb = FirebaseFirestore.getInstance()

        val presentGrammarRef = firestoreDb.collection("grammar").document("present")

        val presentSimpleRef = presentGrammarRef.collection("simple").document("Desc")
        val presentPerfectRef = presentGrammarRef.collection("perfect").document("desc")
        val presentPerfectionRef =
            presentGrammarRef.collection("perfect_continuous").document("desc")
        val presentContinuousRef = presentGrammarRef.collection("continuous").document("desc")

        _binding = FragmentPresentBinding.inflate(inflater, container, false)

  /*      binding.tvMutation.setOnClickListener {
            // Přepnutí proměnné isEnglish
            isEnglish = !isEnglish
            // Aktualizace textu tlačítka pro přepínání jazyka
            binding.tvMutation.text = if (isEnglish) "EN" else "CZ"
            // Volání loadTense pro načtení aktualizovaných dat
            loadTense(binding, presentGrammarRef, presentSimpleRef, isEnglish)
        }*/

        // Načtení výchozího jazyka
        loadTense(binding, presentGrammarRef, presentSimpleRef, isEnglish)

        binding.var1.text = "Present Simple"
        binding.var2.text = "Present Continuous"
        binding.var3.text = "Present Perfect"
        binding.var4.text = "Present Present Continuous"

        binding.showMore.setOnClickListener {
            binding.tvUsageCz.maxLines = Int.MAX_VALUE
        }



        // Nastavení posluchačů pro RadioButtony
        binding.tenseRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.var_1 -> {
                    loadTense(binding, presentGrammarRef, presentSimpleRef, isEnglish)
                }
                R.id.var_2 -> {
                    loadTense(binding, presentGrammarRef, presentContinuousRef, isEnglish)
                }
                R.id.var_3 -> {
                    loadTense(binding, presentGrammarRef, presentPerfectRef, isEnglish)
                }
                R.id.var_4 -> {
                    loadTense(binding, presentGrammarRef, presentPerfectionRef, isEnglish)
                }
            }
        }

        return binding.root
    }


}



private fun loadTense(
    binding: FragmentPresentBinding,
    tenseGrammarRef: DocumentReference,
    specTenseRef: DocumentReference,
    english: Boolean
) {
    tenseGrammarRef.get().addOnSuccessListener { _ ->
        specTenseRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                if (english){

                // Získání hodnot z dokumentu
                binding.apply {
                    enName.text = documentSnapshot.getString("name") ?: "Default value for test1"
                    czName.text = documentSnapshot.getString("namecz") ?: "Default value for test2"
                    tvUsage.text = documentSnapshot.getString("usage") ?: "Usage of tense"
                    tvForm.text = documentSnapshot.getString("form") ?: "Form of tense"
                    tvAffirm.text = documentSnapshot.getString("affirm") ?: "Affirmation form"
                    tvNegativ.text = documentSnapshot.getString("negative") ?: "Negative form"
                    tvQuestion.text = documentSnapshot.getString("questcz") ?: "Question form"
                    tvAExample.text = documentSnapshot.getString("aEx") ?: "Example of affirmation"
                    tvNExample.text = documentSnapshot.getString("nEx") ?: "Example of negative"
                    tvQExample.text = documentSnapshot.getString("qEX") ?: "Example of question"
                    tvUsageCz.text = documentSnapshot.getString("usagecz") ?: "Usage of tense"
                }

                }
            } else {
                binding.apply {
                    enName.text = documentSnapshot.getString("name") ?: "Default value for test1"
                    czName.text = documentSnapshot.getString("namecz") ?: "Default value for test2"
                    tvUsage.text = documentSnapshot.getString("usagecz") ?: "Usage of tense"
                    tvForm.text = documentSnapshot.getString("formcz") ?: "Form of tense"
                    tvAffirm.text = documentSnapshot.getString("affirmcz") ?: "Affirmation form"
                    tvNegativ.text = documentSnapshot.getString("negativecz") ?: "Negative form"
                    tvQuestion.text = documentSnapshot.getString("questcz") ?: "Question form"
                    tvAExample.text = documentSnapshot.getString("aExcz") ?: "Example of affirmation"
                    tvNExample.text = documentSnapshot.getString("nExcz") ?: "Example of negative"
                    tvQExample.text = documentSnapshot.getString("qEXcz") ?: "Example of question"
                }
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error getting document: ", exception)
        }
    }
}



