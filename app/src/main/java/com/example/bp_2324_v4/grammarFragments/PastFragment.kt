package com.example.bp_2324_v4.grammarFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.databinding.FragmentPastBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class PastFragment : Fragment() {
    private var _binding: FragmentPastBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestoreDb: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {


        firestoreDb = FirebaseFirestore.getInstance()

        val presentGrammarRef = firestoreDb.collection("grammar").document("present")
        val pastGrammarRef = firestoreDb.collection("grammar").document("past")
        val futureGrammarRef = firestoreDb.collection("grammar").document("future")

        val presentSimpleRef = presentGrammarRef.collection("simple").document("Desc")
        val presentPerfectRef = presentGrammarRef.collection("perfect").document("desc")
        val presentPerfectcontRef =
            presentGrammarRef.collection("perfect_continuous").document("desc")
        val presentContinuousRef = presentGrammarRef.collection("continuous").document("desc")

        val pastSimpleRef = pastGrammarRef.collection("simple").document("desc")
        val pastPerfectRef = pastGrammarRef.collection("perfect").document("desc")
        val pastPerfectcontRef = pastGrammarRef.collection("perfect_continuous").document("desc")
        val pastContinuousRef = pastGrammarRef.collection("continuous").document("desc")

        val futureSimpleRef = futureGrammarRef.collection("simple").document("Desc")
        val futurePerfectRef = futureGrammarRef.collection("perfect").document("desc")
        val futurePerfectcontRef =
            futureGrammarRef.collection("perfect_continuous").document("desc")
        val futureContinuousRef = futureGrammarRef.collection("continuous").document("Desc")


        _binding = FragmentPastBinding.inflate(inflater, container, false)

        val tense = arguments?.getString("tense")
        when (tense) {
            "present" -> {
                loadTense(binding, presentGrammarRef, presentSimpleRef)

                binding.var1.text = "Present Simple"
                binding.var2.text = "Present Continuous"
                binding.var3.text = "Present Perfect"
                binding.var4.text = "Present Present Continuous"


                binding.tenseRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.var_1 -> {
                            loadTense(binding, presentGrammarRef, presentSimpleRef)


                        }

                        R.id.var_2 -> {
                            loadTense(binding, presentGrammarRef, presentContinuousRef)
                        }

                        R.id.var_3 -> {
                            loadTense(binding, presentGrammarRef, presentPerfectRef)
                        }

                        R.id.var_4 -> {
                            loadTense(binding, presentGrammarRef, presentPerfectcontRef)
                        }
                    }
                }
            }

            "past" -> {
                loadTense(binding, presentGrammarRef, presentSimpleRef)

                binding.var1.text = "Past Simple"
                binding.var2.text = "Past Continuous"
                binding.var3.text = "Past Perfect"
                binding.var4.text = "Past\nPresent Continuous"

                loadTense(binding, pastGrammarRef, pastSimpleRef)

                binding.tenseRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.var_1 -> {
                            loadTense(binding, pastGrammarRef, pastSimpleRef)
                        }

                        R.id.var_2 -> {
                            loadTense(binding, pastGrammarRef, pastContinuousRef)
                        }

                        R.id.var_3 -> {
                            loadTense(binding, pastGrammarRef, pastPerfectRef)
                        }

                        R.id.var_4 -> {
                            loadTense(binding, pastGrammarRef, pastPerfectcontRef)
                        }
                    }
                }
            }

            "future" -> {
                binding.var1.text = "Future Simple"
                binding.var2.text = "Future Continuous"
                binding.var3.text = "Future Perfect"
                binding.var4.text = "Future Perfect Continuous"

                // Načtěte a zobrazte obsah pro future tense
                loadTense(binding, futureGrammarRef, futureSimpleRef)

                binding.tenseRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.var_1 -> {
                            loadTense(binding, futureGrammarRef, futureSimpleRef)
                        }

                        R.id.var_2 -> {
                            loadTense(binding, futureGrammarRef, futureContinuousRef)
                        }

                        R.id.var_3 -> {
                            loadTense(binding, futureGrammarRef, futurePerfectRef)
                        }

                        R.id.var_4 -> {
                            loadTense(binding, futureGrammarRef, futurePerfectcontRef)
                        }
                    }
                }
            }
        }
        return binding.root

    }


}

private fun loadTense(
    binding: FragmentPastBinding,
    tenseGrammarRef: DocumentReference,
    specTenseRef: DocumentReference
) {
    tenseGrammarRef.get().addOnSuccessListener { _ ->
        specTenseRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null && documentSnapshot.exists()) {

                if (binding.tvMutation.text == "EN"){
                    // Získá hodnoty z dokumentu EN
                    binding.apply {
                        enName.text =
                            documentSnapshot.getString("name")
                                ?: "Default value for test1"
                        czName.text =
                            documentSnapshot.getString("namecz")
                                ?: "Default value for test2"

                        tvUsage.text = documentSnapshot.getString("usage") ?: "Usage of tense"
                        tvForm.text = documentSnapshot.getString("form") ?: "Form of tense"

                        tvAffirm.text = documentSnapshot.getString("affirm") ?: "Affirmation form"
                        tvNegativ.text = documentSnapshot.getString("negative") ?: "Negativ form"
                        tvQuestion.text = documentSnapshot.getString("quest") ?: "Question form"

                        tvAExample.text = documentSnapshot.getString("aEx") ?: "Example of affirmation"
                        tvNExample.text = documentSnapshot.getString("nEx") ?: "Example of negative"
                        tvQExample.text = documentSnapshot.getString("qEX") ?: "Example of question"

                    }
                } else {
                    // Získá hodnoty z dokumentu CZ
                    binding.apply {
                        enName.text =
                            documentSnapshot.getString("name")
                                ?: "Default value for test1"
                        czName.text =
                            documentSnapshot.getString("namecz")
                                ?: "Default value for test2"

                        tvUsage.text = documentSnapshot.getString("usagecz") ?: "Usage of tense"
                        tvForm.text = documentSnapshot.getString("formcz") ?: "Form of tense"

                        tvAffirm.text = documentSnapshot.getString("affirmcz") ?: "Affirmation form"
                        tvNegativ.text = documentSnapshot.getString("negativecz") ?: "Negativ form"
                        tvQuestion.text = documentSnapshot.getString("questcz") ?: "Question form"

                        tvAExample.text = documentSnapshot.getString("aExcz") ?: "Example of affirmation"
                        tvNExample.text = documentSnapshot.getString("nExcz") ?: "Example of negative"
                        tvQExample.text = documentSnapshot.getString("qExcz") ?: "Example of question"

                    }
                }

            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Chyba při získávání dokumentu: ", exception)
        }
    }
}
