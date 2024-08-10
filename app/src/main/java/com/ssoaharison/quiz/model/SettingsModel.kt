package com.ssoaharison.quiz.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsModel(
    var number: Int = 10,
    var category: Int = 0,
    var difficulty: String = "",
    var type: String = "",
): Parcelable