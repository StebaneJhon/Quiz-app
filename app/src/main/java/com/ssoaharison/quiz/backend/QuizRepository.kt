package com.ssoaharison.quiz.backend

class QuizRepository constructor(private val retrofitClient: RetrofitClient) {
    suspend fun getQuizQuestionMultiple(
        amount: String,
        category: String,
        difficulty: String,
        type: String
        ) = retrofitClient.instance.getQuizQuestionMultiple(amount, category, difficulty, type)
}