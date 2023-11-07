package com.ssoaharison.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.backend.RetrofitService
import com.ssoaharison.quiz.databinding.ActivityMainBinding
import com.ssoaharison.quiz.model.QuizQuestionMultipleViewModel
import com.ssoaharison.quiz.model.QuizQuestionMultipleViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var quizViewModel: QuizQuestionMultipleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofitService = RetrofitService.getInstance()
        val repository = QuizRepository(retrofitService)
        quizViewModel = ViewModelProvider(
            this,
            QuizQuestionMultipleViewModelFactory(repository)
        ).get(QuizQuestionMultipleViewModel::class.java)

        quizViewModel.getQuizQuestionMultiple(10, "multiple")
        quizViewModel.questionList.observe(this) {
            val data = it
            val a = 1
        }

        quizViewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        quizViewModel.loading.observe(this, Observer {
            if (it) {
                //binding.progressDialog.visibility = View.VISIBLE
            } else {
                //binding.progressDialog.visibility = View.GONE
            }
        })
    }
}