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

        val pastGrammarRef = firestoreDb.collection("grammar").document("past")

        val pastSimpleRef = pastGrammarRef.collection("simple").document("desc")
        val pastPerfectRef = pastGrammarRef.collection("perfect").document("desc")
        val pastPerfectcontRef = pastGrammarRef.collection("perfect_continuous").document("desc")
        val pastContinuousRef = pastGrammarRef.collection("continuous").document("desc")



        _binding = FragmentPastBinding.inflate(inflater, container, false)

        loadTense(binding, pastGrammarRef, pastSimpleRef)


        binding.var1.text = "Past Simple"
        binding.var2.text = "Past Continuous"
        binding.var3.text = "Past Perfect"
        binding.var4.text = "Past\nPerfect Continuous"

        loadTense(binding, pastGrammarRef, pastSimpleRef)

        // Nastavení posluchačů pro RadioButtony
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

        binding.cwUsage.setOnClickListener{
            val v =  if (binding.tvUsageCz.visibility == View.GONE) View.VISIBLE else View.GONE
            binding.tvUsageCz.visibility = v
        }

        binding.cwForm.setOnClickListener{
            val v =  if (binding.tvFormCZ.visibility == View.GONE) View.VISIBLE else View.GONE
            binding.tvFormCZ.visibility = v
        }

        binding.cwStructure.setOnClickListener{
            val v =  if (binding.tvStructureCz.visibility == View.GONE) View.VISIBLE else View.GONE
            binding.tvStructureCz.visibility = v
        }


return binding.root

}

    private fun loadTense(
        binding: FragmentPastBinding,
        tenseGrammarRef: DocumentReference,
        specTenseRef: DocumentReference
    ) {
        tenseGrammarRef.get().addOnSuccessListener { _ ->
            specTenseRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {

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


                    binding.apply {
                        tvUsageCz.text = documentSnapshot.getString("usagecz") ?: "Usage of tense"
                        tvFormCZ.text = documentSnapshot.getString("formcz") ?: "Form of tense"
                        tvAffirmCz.text = documentSnapshot.getString("affirmcz") ?: "Affirmation form"
                        tvNegativCz.text = documentSnapshot.getString("negativecz") ?: "Negative form"
                        tvQuestionCz.text = documentSnapshot.getString("questcz") ?: "Question form"
                        tvAExamplecz.text =
                            documentSnapshot.getString("aExcz") ?: "Example of affirmation"
                        tvNExampleCz.text = documentSnapshot.getString("nExcz") ?: "Example of negative"
                        tvQExampleCz.text = documentSnapshot.getString("qEXcz") ?: "Example of question"
                    }
                }

            }.addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting document: ", exception)
            }
        }
    }


}
