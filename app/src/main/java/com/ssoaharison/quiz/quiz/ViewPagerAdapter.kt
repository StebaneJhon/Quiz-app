package com.ssoaharison.quiz.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.model.Result
import com.ssoaharison.quiz.util.QUESTION_FONT_TINTS

class ViewPagerAdapter(
    val questions: List<Result>,
    val appContext: Context,
    private val userChoice: (List<Any>) -> Unit
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
            position + 1,
            userChoice
        )
    }

    inner class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val tvQuestion: TextView = itemView.findViewById(R.id.tvQuizQuestion)
        private val tvProgression: TextView = itemView.findViewById(R.id.tvProgression)
        private val quizQuestionContainer: ConstraintLayout = itemView.findViewById(R.id.cl_quiz_uestion_container)
        private val answer1: MaterialButton = itemView.findViewById(R.id.btAnswer1)
        private val answer2: MaterialButton = itemView.findViewById(R.id.btAnswer2)
        private val answer3: MaterialButton = itemView.findViewById(R.id.btAnswer3)
        private val answer4: MaterialButton = itemView.findViewById(R.id.btAnswer4)

        @SuppressLint("StringFormatMatches")
        fun bind(
            question: Result,
            context: Context,
            questionNumber: Int,
            userChoice: (List<Any>) -> Unit
        ) {
            val questionBackgroundTint = QUESTION_FONT_TINTS.random()
            quizQuestionContainer.backgroundTintList = ContextCompat.getColorStateList(context, questionBackgroundTint)
            Log.e("QuizFragment", question.correctAnswer)
            tvQuestion.text = HtmlCompat.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_LEGACY)
            tvProgression.text = context.getString(
                R.string.progression_text,
                "$questionNumber",
                "${questions.size}"
            )
            val answers = randomizeAnswers(question.incorrectAnswers, question.correctAnswer)
            if (question.incorrectAnswers.size == 2) {
                answer1.text = HtmlCompat.fromHtml(answers[0], HtmlCompat.FROM_HTML_MODE_LEGACY)
                answer2.text = HtmlCompat.fromHtml(answers[1], HtmlCompat.FROM_HTML_MODE_LEGACY)
                answer3.visibility = View.GONE
                answer4.visibility = View.GONE
            } else {
                answer1.text = HtmlCompat.fromHtml(answers[0], HtmlCompat.FROM_HTML_MODE_LEGACY)
                answer2.text = HtmlCompat.fromHtml(answers[1], HtmlCompat.FROM_HTML_MODE_LEGACY)
                answer3.text = HtmlCompat.fromHtml(answers[2], HtmlCompat.FROM_HTML_MODE_LEGACY)
                answer4.text = HtmlCompat.fromHtml(answers[3], HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            answer1.setOnClickListener { userChoice(listOf(question.correctAnswer, answer1.text.toString(), quizQuestionContainer, questionNumber, questionBackgroundTint!!)) }
            answer2.setOnClickListener { userChoice(listOf(question.correctAnswer, answer2.text.toString(), quizQuestionContainer, questionNumber, questionBackgroundTint!!)) }
            answer3.setOnClickListener { userChoice(listOf(question.correctAnswer, answer3.text.toString(), quizQuestionContainer, questionNumber, questionBackgroundTint!!)) }
            answer4.setOnClickListener { userChoice(listOf(question.correctAnswer, answer4.text.toString(), quizQuestionContainer, questionNumber, questionBackgroundTint!!)) }
        }
    }

    fun randomizeAnswers(incorrectAnswers: List<String>, correctAnswer: String): List<String> {
        val result = incorrectAnswers + correctAnswer
        return result.shuffled()
    }

}