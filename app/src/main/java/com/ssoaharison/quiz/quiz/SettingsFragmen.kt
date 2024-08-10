package com.ssoaharison.quiz.quiz

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.model.SettingsModel
import com.ssoaharison.quiz.util.QuizCategoryHelper

class SettingsFragment(): DialogFragment() {

    private var tvCategory: AutoCompleteTextView? = null
    private var tvDifficulty: AutoCompleteTextView? = null
    private var tvType: AutoCompleteTextView? = null
    private var tvNumber: TextInputEditText? = null
    private var btDismiss: MaterialButton? = null
    private var btApply: MaterialButton? = null

    internal lateinit var listener: SettingsFragmentListener

    interface SettingsFragmentListener {
        fun onSettingsDialogPositiveClick(settings: SettingsModel)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.ly_settings, null)

        tvCategory = view?.findViewById(R.id.tv_category)
        tvDifficulty = view?.findViewById(R.id.tv_difficulty)
        tvType = view?.findViewById(R.id.tv_type)
        tvNumber = view?.findViewById(R.id.tv_question_number)
        btDismiss = view?.findViewById(R.id.bt_back_to_quiz)
        btApply = view?.findViewById(R.id.bt_apply)

        (tvCategory as? MaterialAutoCompleteTextView)?.setSimpleItems(QuizCategoryHelper().getCategories())
        (tvDifficulty as? MaterialAutoCompleteTextView)?.setSimpleItems(QuizCategoryHelper().getDifficulty())
        (tvType as? MaterialAutoCompleteTextView)?.setSimpleItems(QuizCategoryHelper().getType())

        btDismiss?.setOnClickListener { dismiss() }
        btApply?.setOnClickListener {
            val questionSum = tvNumber?.text
            if (questionSum.isNullOrEmpty() || questionSum.toString().toInt() < 5) {
                tvNumber?.error = "Questions must be 5 or more for a better experience."
            } else {
                val settingsModel = SettingsModel(
                    number = questionSum.toString().toInt(),
                    category = QuizCategoryHelper().selectCategory(tvCategory?.text.toString()),
                    difficulty = tvDifficulty?.text.toString().lowercase(),
                    type = QuizCategoryHelper().decodeType(tvType?.text.toString())
                )
                listener.onSettingsDialogPositiveClick(settingsModel)
//                setFragmentResult("requestKey", bundleOf("requestKey" to settingsModel))
                dismiss()
            }
        }

        builder.setView(view)
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SettingsFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("${context.toString()} must implement SettingsFragmentListener"))
        }
    }
}