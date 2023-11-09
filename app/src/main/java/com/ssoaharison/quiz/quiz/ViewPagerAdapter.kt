package com.ssoaharison.quiz.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.model.Result

class ViewPagerAdapter(
    val questions: List<Result>,
    val appContext: Context
): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ly_quiz_card, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun getItemCount() = questions.size

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        return holder.bind(
            questions[position],
            appContext,
            position + 1
        )
    }

    inner class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val tvQuestion: TextView = itemView.findViewById(R.id.tvQuizQuestion)
        private val tvProgression: TextView = itemView.findViewById(R.id.tvProgression)
        private val answer1: MaterialButton = itemView.findViewById(R.id.btAnswer1)
        private val answer2: MaterialButton = itemView.findViewById(R.id.btAnswer2)
        private val answer3: MaterialButton = itemView.findViewById(R.id.btAnswer3)
        private val answer4: MaterialButton = itemView.findViewById(R.id.btAnswer4)

        @SuppressLint("StringFormatMatches")
        fun bind(
            question: Result,
            context: Context,
            questionNumber: Int
        ) {
            tvQuestion.text = question.question
            tvProgression.text = context.getString(
                R.string.progression_text,
                "$questionNumber",
                "${questions.size}"
            )
            answer1.text = question.incorrectAnswers[0]
            answer2.text = question.incorrectAnswers[1]
            answer3.text = question.incorrectAnswers[2]
            answer4.text = question.correctAnswer
        }

    }

}