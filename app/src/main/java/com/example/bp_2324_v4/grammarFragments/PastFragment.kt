package com.example.bp_2324_v4.grammarFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.databinding.FragmentPastBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class PastFragment : Fragment() {
    private var _binding: FragmentPastBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestoreDb: FirebaseFirestore

    @SuppressLint("SetTextI18n")
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
                LoadTense(binding, presentGrammarRef, presentSimpleRef)

                binding.var1.text = "Present Simple"
                binding.var2.text = "Present Continuous"
                binding.var3.text = "Present Perfect"
                binding.var4.text = "Present Present Continuous"


                binding.tenseRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.var_1 -> {
                            LoadTense(binding, pastGrammarRef, presentSimpleRef)
                        }

                        R.id.var_2 -> {
                            LoadTense(binding, pastGrammarRef, presentContinuousRef)
                        }

                        R.id.var_3 -> {
                            LoadTense(binding, pastGrammarRef, presentPerfectRef)
                        }

                        R.id.var_4 -> {
                            LoadTense(binding, pastGrammarRef, presentPerfectcontRef)
                        }
                    }
                }
            }

            "past" -> {
                LoadTense(binding, presentGrammarRef, presentSimpleRef)

                binding.var1.text = "Past Simple"
                binding.var2.text = "Past Continuous"
                binding.var3.text = "Past Perfect"
                binding.var4.text = "Past\nPresent Continuous"

                LoadTense(binding, pastGrammarRef, pastSimpleRef)

                binding.tenseRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.var_1 -> {
                            LoadTense(binding, pastGrammarRef, pastSimpleRef)
                        }

                        R.id.var_2 -> {
                            LoadTense(binding, pastGrammarRef, pastContinuousRef)
                        }

                        R.id.var_3 -> {
                            LoadTense(binding, pastGrammarRef, pastPerfectRef)
                        }

                        R.id.var_4 -> {
                            LoadTense(binding, pastGrammarRef, pastPerfectcontRef)
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
                LoadTense(binding, futureGrammarRef, futureSimpleRef)

                binding.tenseRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.var_1 -> {
                            LoadTense(binding, futureGrammarRef, futureSimpleRef)
                        }

                        R.id.var_2 -> {
                            LoadTense(binding, futureGrammarRef, futureContinuousRef)
                        }

                        R.id.var_3 -> {
                            LoadTense(binding, futureGrammarRef, futurePerfectRef)
                        }

                        R.id.var_4 -> {
                            LoadTense(binding, futureGrammarRef, futurePerfectcontRef)
                        }
                    }
                }
            }
        }
        return binding.root

    }


}

private fun LoadTense(
    binding: FragmentPastBinding,
    tenseGrammarRef: DocumentReference,
    specTenseRef: DocumentReference
) {
    tenseGrammarRef.get().addOnSuccessListener { _ ->
        specTenseRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Získá hodnoty z dokumentu
                binding.enName.text =
                    documentSnapshot.getString("name")
                        ?: "Default value for test1"
                binding.czName.text =
                    documentSnapshot.getString("namecz")
                        ?: "Default value for test2"

            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Chyba při získávání dokumentu: ", exception)
        }
    }
}
