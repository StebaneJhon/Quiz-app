package com.ssoaharison.quiz.quiz

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.backend.RetrofitClient
import com.ssoaharison.quiz.databinding.FragmentQuizBinding
import com.ssoaharison.quiz.model.QuizQuestionMultipleViewModel
import com.ssoaharison.quiz.model.QuizQuestionMultipleViewModelFactory

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var appContext: Context? = null

    private val quizViewModel by lazy {
        val repository = QuizRepository(RetrofitClient)
        ViewModelProvider(this, QuizQuestionMultipleViewModelFactory(repository))[QuizQuestionMultipleViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        appContext = container?.context
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizViewModel.getQuizQuestionMultiple(10, 0, "", "multiple")
        quizViewModel.questionList.observe(viewLifecycleOwner) {
            val _adapter = ViewPagerAdapter(
                it.results,
                appContext!!
            )
            binding.viewPager.apply {
                adapter = _adapter
            }
        }

        quizViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
        }

        quizViewModel.loading.observe(viewLifecycleOwner,  Observer {
            if (it) {
                //binding.progressDialog.visibility = View.VISIBLE
            } else {
                //binding.progressDialog.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}