package com.example.bp_2324_v4

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bp_2324_v4d.model.Lesson


class LessonAdapter(private val lessons: List<Lesson>) : RecyclerView.Adapter<LessonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lesson_item, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.tvLessonNumV.text = lesson.lessonNum

        holder.itemView.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val practiceLessonFrag = PracticeLessonFragment()

            // Předání čísla lekce jako argumentu
            val bundle = Bundle()
            bundle.putString("lessonNum", lesson.lessonNum) // předání čísla lekce
            practiceLessonFrag.arguments = bundle

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, practiceLessonFrag)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = lessons.size
}