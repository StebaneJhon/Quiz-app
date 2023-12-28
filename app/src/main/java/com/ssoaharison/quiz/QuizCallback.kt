package com.ssoaharison.quiz

import com.ssoaharison.quiz.model.SettingsModel

interface QuizCallback {
    fun getSettingsModel(): SettingsModel
}