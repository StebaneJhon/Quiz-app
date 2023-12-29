package com.ssoaharison.quiz

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ThemeUtils
import androidx.appcompat.widget.ThemeUtils.getThemeAttrColor
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.color.MaterialColors
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.backend.RetrofitClient
import com.ssoaharison.quiz.databinding.ActivityMainBinding
import com.ssoaharison.quiz.model.Result
import com.ssoaharison.quiz.model.SettingsModel
import com.ssoaharison.quiz.quiz.QuizFragment
import com.ssoaharison.quiz.quiz.QuizQuestionMultipleViewModel
import com.ssoaharison.quiz.quiz.QuizQuestionMultipleViewModelFactory
import com.ssoaharison.quiz.quiz.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var quizFragment: QuizFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.statusBarColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceContainerLowest, Color.BLACK)
        if (savedInstanceState == null) {
            startQuizFragment()
        }

    }

    private fun startQuizFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            quizFragment = QuizFragment()
            add(R.id.fragment_container_view, quizFragment!!)
        }
    }
}