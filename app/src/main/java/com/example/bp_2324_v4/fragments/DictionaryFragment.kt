package com.example.bp_2324_v4.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bp_2324_v4.AddWordFragment
import com.example.bp_2324_v4.R
import com.example.bp_2324_v4.model.Word
import com.example.bp_2324_v4.WordAdapter
import com.example.bp_2324_v4.databinding.FragmentDictionaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentUserId: String? = null
    private var enMain = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)

        // Inicializujeme Firestore
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser?.uid

        // progressBar
        binding.progressBar.visibility = View.VISIBLE

        // Zde získáte data ze své kolekce a aktualizujete RecyclerView
        loadWords()


        binding.btnAddWord.setOnClickListener {
            val addWordFragment = AddWordFragment()
            val transaction = parentFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment_container, addWordFragment)
            transaction.addToBackStack(null)

            transaction.commit()
        }

        binding.btnEnXCz.setOnClickListener {
            enMain = !enMain
            loadWords()
        }


        // Přidání ItemTouchHelper k RecyclerView
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Zde nic neděláme, protože nechceme umožnit přesouvání položek
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Získání pozice položky, která byla potažena
                val position = viewHolder.adapterPosition
                // Odstranění položky z adapteru
                (binding.recyclerWords.adapter as? WordAdapter)?.deleteItem(position)

                // Aktualizace počtu slov a lekcí uživatele
                updateUserWordCount()
                updateUserLevelCount()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerWords)


        return binding.root
    }


    private fun loadWords() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        if (enMain) {
            firestore.collection("users").document(userId).collection("lessons")
                .get()
                .addOnSuccessListener { lessonsSnapshot ->
                    val allWords = mutableListOf<Word>()
                    for (lessonDoc in lessonsSnapshot) {
                        val words = lessonDoc.get("words") as List<Map<String, String>>?
                        words?.forEach { wordMap ->
                            allWords.add(
                                Word(
                                    czech = wordMap["czech"] ?: "",
                                    english = wordMap["english"] ?: "",
                                    lessonNum = lessonDoc.id,
                                    userId = userId,
                                    dbWordId = wordMap["dbWordId"] ?: ""
                                )
                            )
                        }
                    }
                    val adapter = WordAdapter(allWords)
                    binding.recyclerWords.layoutManager = LinearLayoutManager(context)
                    binding.recyclerWords.adapter = adapter
                    binding.progressBar.visibility = View.GONE

                    updateUserWordCount()
                    updateUserLevelCount()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        context,
                        "Error loading words: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        } else {

            firestore.collection("users").document(userId).collection("lessons")
                .get()
                .addOnSuccessListener { lessonsSnapshot ->
                    val allWords = mutableListOf<Word>()
                    for (lessonDoc in lessonsSnapshot) {
                        val words = lessonDoc.get("words") as List<Map<String, String>>?
                        words?.forEach { wordMap ->
                            allWords.add(
                                Word(
                                    czech = wordMap["english"] ?: "",
                                    english = wordMap["czech"] ?: "",
                                    lessonNum = lessonDoc.id,
                                    userId = userId,
                                    dbWordId = wordMap["dbWordId"] ?: ""
                                )
                            )
                        }
                    }
                    val adapter = WordAdapter(allWords)
                    binding.recyclerWords.layoutManager = LinearLayoutManager(context)
                    binding.recyclerWords.adapter = adapter
                    binding.progressBar.visibility = View.GONE

                    updateUserWordCount()
                    updateUserLevelCount()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        context,
                        "Error loading words: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        loadWords()
    }

    private fun updateUserLevelCount() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).collection("lessons")
                .get()
                .addOnSuccessListener { lessonsSnapshot ->
                    val levelCount = lessonsSnapshot.size() // Počet lekcí
                    val userRef = firestore.collection("users").document(userId)

                    // Aktualizace dokumentu uživatele s novým počtem levelů
                    userRef.update("lessons", levelCount.toString())
                        .addOnSuccessListener {
                            //TODO Log.d("UpdateUser", "User level count updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            //TODO Log.e("UpdateUser", "Error updating user level count", e)
                        }
                }
                .addOnFailureListener { e ->
                    //TODO Log.e("UpdateUser", "Error getting lessons", e)
                }
        } else {
            //TODO Log.d("UpdateUser", "User not logged in")
        }
    }

    private fun updateUserWordCount() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).collection("lessons")
                .get()
                .addOnSuccessListener { lessonsSnapshot ->
                    var wordCount = 0
                    for (lessonDoc in lessonsSnapshot) {
                        val words = lessonDoc.get("words") as List<Map<String, String>>?
                        wordCount += words?.size ?: 0
                    }

                    val userRef = firestore.collection("users").document(userId)

                    // Aktualizace dokumentu uživatele s novým počtem slovíček
                    userRef.update("words", wordCount.toString())
                        .addOnSuccessListener {
                            //TODO Log.d("UpdateUser", "User word count updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            //TODO Log.e("UpdateUser", "Error updating user word count", e)
                        }
                }
                .addOnFailureListener { e ->
                    //TODO Log.e("UpdateUser", "Error getting lessons", e)
                }
        } else {
            //TODO Log.d("UpdateUser", "User not logged in")
        }
    }
}