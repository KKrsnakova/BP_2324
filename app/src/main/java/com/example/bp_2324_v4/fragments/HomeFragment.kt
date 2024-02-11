package com.example.bp_2324_v4.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bp_2324_v4.LogInActivity
import com.example.bp_2324_v4.MainActivity
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.databinding.ActivityLogInBinding
import com.example.bp_2324_v4.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            logoutButton.setOnClickListener {
                firebaseAuth.signOut()

                // Close the fragment and open LogInActivity
                val intent = Intent(requireActivity(), LogInActivity::class.java)
                startActivity(intent)

                // Optionally, if you want to clear the activity stack (so users can't navigate back to the fragment with the back button), you can add these flags
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                // Finish the current activity if it should not stay in the back stack after logging out
                activity?.finish()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}

