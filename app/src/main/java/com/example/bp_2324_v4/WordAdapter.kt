package com.example.bp_2324_v4

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bp_2324_v4.model.Word
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class WordAdapter(
    private val context: Context,
    private var words: List<Word>
) : RecyclerView.Adapter<WordViewHolder>() {

    private val mutableWords = words.toMutableList()
    private lateinit var textSpeech: TextToSpeech
    private var buttonVisible = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {


        val currentWord = mutableWords[position]
        holder.tvTitle.text = currentWord.english
        holder.tvLessonNum.text = currentWord.lessonNum
        holder.tvName.text = currentWord.czech

        holder.btnTextToSpeech.setOnClickListener {
            textToSpeechNow(currentWord.english)
        }

        if (buttonVisible){
            holder.btnTextToSpeech.visibility = View.VISIBLE
        }else{
            holder.btnTextToSpeech.visibility = View.GONE
        }


    }

    private fun textToSpeechNow(english: String) {
        textSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textSpeech.setLanguage(Locale.UK)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "Language not supported", Toast.LENGTH_SHORT).show()
                } else {
                    textSpeech.speak(english, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            } else {
                Toast.makeText(context, "Initialization failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = mutableWords.size

    fun deleteItem(position: Int) {
        val wordToDelete = mutableWords[position]
        val userId = wordToDelete.userId
        val lessonNum = wordToDelete.lessonNum
        val wordMap = hashMapOf("czech" to wordToDelete.czech, "english" to wordToDelete.english)

        val lessonRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("lessons").document(lessonNum)

        lessonRef.update("words", FieldValue.arrayRemove(wordMap))
            .addOnSuccessListener {
                lessonRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val words = documentSnapshot.get("words") as List<*>?
                        if (words.isNullOrEmpty()) {
                            lessonRef.delete()
                        }
                    }

                    mutableWords.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Error loading words: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun setBtnVisibility(visible: Boolean){
        buttonVisible = visible
        notifyDataSetChanged()
    }
}