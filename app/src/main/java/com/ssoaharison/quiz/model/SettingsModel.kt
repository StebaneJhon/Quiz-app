package com.ssoaharison.quiz.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsModel(
    var number: Int? = null,
    var category: Int? = null,
    var difficulty: String? = null,
    var type: String? = null
): Parcelable