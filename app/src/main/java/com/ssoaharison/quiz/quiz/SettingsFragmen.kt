package com.ssoaharison.quiz.quiz

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.util.QuizCategoryHelper

class SettingsFragment(): AppCompatDialogFragment() {

    private var tfCategory: AutoCompleteTextView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.ly_settings, null)
        tfCategory = view?.findViewById(R.id.tv_category)

        val items = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")
        (tfCategory as? MaterialAutoCompleteTextView)?.setSimpleItems(QuizCategoryHelper().getCategories())
        builder.setView(view)
        return builder.create()
    }
}