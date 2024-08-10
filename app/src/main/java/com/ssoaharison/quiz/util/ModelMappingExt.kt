package com.ssoaharison.quiz.util

import com.ssoaharison.quiz.model.ExternalResult
import com.ssoaharison.quiz.model.Result


fun Result.toExternal() = ExternalResult(
    category = category,
    correctAnswer = correctAnswer,
    difficulty = difficulty,
    incorrectAnswers = incorrectAnswers,
    question = question,
    type = type,
    backgroundColor = QUESTION_FONT_TINTS.random(),
)

@JvmName("localToExternal")
fun List<Result>.toExternal() = map(Result::toExternal)