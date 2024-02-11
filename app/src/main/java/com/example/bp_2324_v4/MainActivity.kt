package com.example.bp_2324_v4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.databinding.ActivityMainBinding
import com.example.bp_2324_v4.fragments.DictionaryFragment
import com.example.bp_2324_v4.fragments.GrammarFragment
import com.example.bp_2324_v4.fragments.HomeFragment
import com.example.bp_2324_v4.fragments.ProfileFragment
import com.example.bp_2324_v4.fragments.PracticeFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        // Nastavení výchozího fragmentu
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        binding.apply {
            navView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> replace(HomeFragment())
                    R.id.navigation_dictionary -> replace(DictionaryFragment())
                    R.id.navigation_grammar -> replace(GrammarFragment())
                    R.id.navigation_profile -> replace(ProfileFragment())
                    R.id.navigation_settings -> replace(PracticeFragment())
                }
                true
            }

            // Nastavení položky menu "Home" jako vybrané
            navView.menu.findItem(R.id.navigation_home)?.isChecked = true



            btnLogOut.setOnClickListener {
                firebaseAuth.signOut()

                // Close the fragment and open LogInActivity
                val intent = Intent(baseContext, LogInActivity::class.java)
                startActivity(intent)

                // Optionally, if you want to clear the activity stack (so users can't navigate back to the fragment with the back button), you can add these flags
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                // Finish the current activity if it should not stay in the back stack after logging out
                finish()
            }
        }
    }


    private fun replace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
