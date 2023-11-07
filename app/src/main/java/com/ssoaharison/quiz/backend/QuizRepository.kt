package com.ssoaharison.quiz.backend

class QuizRepository constructor(private val retrofitService: RetrofitService) {
    suspend fun getQuizQuestionMultiple(amount: String, type: String) = retrofitService.getQuizQuestionMultiple(amount, type)
}