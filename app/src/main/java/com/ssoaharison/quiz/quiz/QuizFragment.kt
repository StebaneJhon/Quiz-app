package com.ssoaharison.quiz.quiz

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.backend.RetrofitClient
import com.ssoaharison.quiz.databinding.FragmentQuizBinding
import com.ssoaharison.quiz.model.Result
import com.ssoaharison.quiz.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var appContext: Context? = null
    private lateinit var animFadeIn: Animation
    private lateinit var animFadeOut: Animation
    private var quizQuestionContainer: ConstraintLayout? = null
    private var userScore: Int = 0
    private var round: Int = 0

    private val quizViewModel by lazy {
        val repository = QuizRepository(RetrofitClient)
        ViewModelProvider(
            this,
            QuizQuestionMultipleViewModelFactory(repository)
        )[QuizQuestionMultipleViewModel::class.java]
    }

    companion object {
        private const val TAG = "QuizFragment"
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

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.isUserInputEnabled = false
        animFadeIn = AnimationUtils.loadAnimation(appContext, R.anim.fade_in)
        animFadeOut = AnimationUtils.loadAnimation(appContext, R.anim.fade_out)
        launchQuiz()

        binding.btRestart.setOnClickListener {
            launchQuiz()
        }


    }

    private fun launchQuiz() {
        userScore = 0
        round = 0
        quizQuestionContainer = getView()?.findViewById(R.id.clQuizQuestionContainer)
        binding.tvUserScore.text = getString(R.string.text_score, "$userScore")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                quizViewModel.apply {
                    getQuizQuestionMultiple(10, 0, "", "multiple")
                    questionList.collect {
                        when (it) {
                            is UiState.Loading -> {
                                binding.cvLoading.isVisible = true
                            }

                            is UiState.Error -> {
                                binding.cvLoading.isVisible = false
                                Log.e(TAG, it.errorMessage)
                            }

                            is UiState.Success -> {
                                userScore = 0
                                round = 0
                                binding.cvLoading.isVisible = false
                                startQuiz(it.data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startQuiz(questionList: List<Result>){
        setScore(questionList)
        val _adapter = ViewPagerAdapter(questionList, appContext!!) {
            if (quizViewModel.isUserChoiceCorrect(it)) {
                if ( binding.viewPager.currentItem < round) {
                    Snackbar.make(binding.root, "You finished the Quiz!", Snackbar.LENGTH_LONG).show()
                } else {

                    userScore += 1
                    round += 1
                    binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
                    setScore(questionList)
                }
            } else {
                giveFeedback(it[2] as View, R.drawable.quiz_question_container_border_on_wrong)
            }
        }
        binding.viewPager.apply {
            adapter = _adapter
        }
    }

    private fun setScore(questionList: List<Result>) {
        val color = ArgbEvaluator().evaluate(
            userScore.toFloat() / questionList.size,
            ContextCompat.getColor(appContext!!, R.color.red700),
            ContextCompat.getColor(appContext!!, R.color.green)
        ) as Int
        binding.tvUserScore.text = getString(R.string.text_score, "$userScore")
        binding.cvUserScoreContainer.setCardBackgroundColor(color)
    }

    private fun giveFeedback(viewToAnimate: View, color: Int) {
        viewToAnimate.startAnimation(animFadeIn)
        viewToAnimate.background = AppCompatResources.getDrawable(appContext!!, color)
        lifecycleScope.launch {
            delay(700)
            viewToAnimate.background = AppCompatResources.getDrawable(appContext!!, R.color.green)
            viewToAnimate.startAnimation(animFadeIn)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}