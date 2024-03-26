package com.example.bp_2324_v4.recyclerOperations

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bp_2324_v4.R

class LessonViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvLessonNumV:TextView=itemView.findViewById(R.id.tvLessonNumV)
    val ivAttempted:ImageView=itemView.findViewById(R.id.ivAttempted)
}