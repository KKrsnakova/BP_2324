package com.example.bp_2324_v4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.databinding.FragmentGrammarBinding
import com.example.bp_2324_v4.grammarFragments.FutureFragment
import com.example.bp_2324_v4.grammarFragments.PastFragment
import com.example.bp_2324_v4.grammarFragments.PresentFragment

class GrammarFragment : Fragment() {
    private var _binding: FragmentGrammarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGrammarBinding.inflate(inflater, container, false)

        binding.apply {
            btnPresent.setOnClickListener {
                transition(PresentFragment())
            }
            btnPast.setOnClickListener {
                transition(PastFragment())
            }
            btnFuture.setOnClickListener {
                transition(FutureFragment())
            }
        }

        return binding.root
    }

    private fun transition(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


}