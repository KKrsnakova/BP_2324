package com.example.bp_2324_v4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.databinding.FragmentGrammarBinding
import com.example.bp_2324_v4.grammarFragments.PastFragment
import com.example.bp_2324_v4.grammarFragments.PresentFragment
import com.google.firebase.firestore.FirebaseFirestore

class GrammarFragment : Fragment() {


    private var _binding: FragmentGrammarBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGrammarBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()

        binding.btnPresent.setOnClickListener {
            val fragment = PresentFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnPast.setOnClickListener {
            val fragment = createFragmentWithArgs("past")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.btnFuture.setOnClickListener {
            val fragment = createFragmentWithArgs("future")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }


        return binding.root
    }

    private fun createFragmentWithArgs(tense: String): Fragment {
        val fragment = PastFragment()  // Nahraďte názvem vašeho univerzálního fragmentu
        fragment.arguments = Bundle().apply {
            putString("tense", tense)
        }
        return fragment
    }


}