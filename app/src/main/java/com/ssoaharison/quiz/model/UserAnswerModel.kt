package com.ssoaharison.quiz.model

import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

data class UserAnswerModel (
    val correctAnswer: String,
    val userAnswer: String,
    val view: MaterialButton,
    val questionNumber: Int,
    val questionBackgroundColor: Int
)