package com.example.bp_2324_v4

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.databinding.ActivityMainBinding
import com.example.bp_2324_v4.fragments.DictionaryFragment
import com.example.bp_2324_v4.fragments.GrammarFragment
import com.example.bp_2324_v4.fragments.PracticeFragment
import com.example.bp_2324_v4.fragments.ProfileFragment
import com.example.bp_2324_v4.fragments.TranslatorFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var isProfileIconClicked = false // Globální proměnná


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        binding = ActivityMainBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        // Nastavení výchozího fragmentu
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        binding.apply {
            navView.setOnItemSelectedListener { menuItem ->
                isProfileIconClicked = false // Resetování proměnné
                when (menuItem.itemId) {
                    R.id.navigation_profile -> replace(ProfileFragment())
                    R.id.navigation_dictionary -> replace(DictionaryFragment())
                    R.id.navigation_grammar -> replace(GrammarFragment())
                    R.id.navigation_translator -> replace(TranslatorFragment())
                    R.id.navigation_settings -> replace(PracticeFragment())
                }
                true
            }

            // Nastavení položky menu "Home" jako vybrané
            navView.menu.findItem(R.id.navigation_profile)?.isChecked = true


            binding.ivLogout.setOnClickListener {
                logout()
            }

        }
    }

    private fun logout() {
        firebaseAuth.signOut()

        // Close the fragment and open LogInActivity
        val intent = Intent(baseContext, LogInActivity::class.java)
        startActivity(intent)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        finish()
    }

    private fun replace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
