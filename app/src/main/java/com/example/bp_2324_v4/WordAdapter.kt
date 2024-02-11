package com.example.bp_2324_v4

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bp_2324_v4.model.Word
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class WordAdapter(private var words: List<Word>) : RecyclerView.Adapter<WordViewHolder>() {

    private val mutableWords = words.toMutableList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val currentWord = words[position]
        holder.tvTitle.text = currentWord.english
        holder.tvLessonNum.text = currentWord.lessonNum
        holder.tvName.text = currentWord.czech
    }

    override fun getItemCount() = words.size

    fun deleteItem(position: Int) {
        val wordToDelete = mutableWords[position]
        val userId = wordToDelete.userId // ID uživatele
        val lessonNum = wordToDelete.lessonNum // Číslo lekce
        val wordMap = hashMapOf("czech" to wordToDelete.czech, "english" to wordToDelete.english)

        // Cesta k dokumentu lekce v databázi Firestore
        val lessonRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("lessons").document(lessonNum)

        // Odstranění slova z lekce
        lessonRef.update("words", FieldValue.arrayRemove(wordMap))
            .addOnSuccessListener {
                // Kontrola, zda jsou v lekci další slovíčka
                lessonRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val words = documentSnapshot.get("words") as List<*>?
                        if (words.isNullOrEmpty()) {
                            // Lekce je prázdná, odstraníme ji
                            lessonRef.delete()
                        }
                    }

                    // Odstranění slovíčka z našeho mutable seznamu a upozornění RecyclerView
                    mutableWords.removeAt(position)
                    notifyItemRemoved(position)
                    // Aktualizace externího neměnného seznamu
                    words = mutableWords.toList()
                }
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

}