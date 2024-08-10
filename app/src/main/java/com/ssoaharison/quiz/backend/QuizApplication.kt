package com.ssoaharison.quiz.backend

import android.app.Application

class QuizApplication: Application() {

    val repository by lazy {
        QuizRepository(RetrofitClient)
    }

}