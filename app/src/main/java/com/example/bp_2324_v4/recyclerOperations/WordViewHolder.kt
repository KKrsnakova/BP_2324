package com.example.bp_2324_v4.recyclerOperations

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bp_2324_v4.R

class WordViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    val tvLessonNum: TextView = itemView.findViewById(R.id.tvLessonNum)
    val tvName: TextView = itemView.findViewById(R.id.tvName)
    val btnTextToSpeech: Button = itemView.findViewById(R.id.btnTextTpSpeach)
}