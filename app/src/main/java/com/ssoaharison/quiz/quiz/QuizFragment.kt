package com.ssoaharison.quiz.quiz

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
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.backend.RetrofitClient
import com.ssoaharison.quiz.databinding.FragmentQuizBinding
import com.ssoaharison.quiz.model.QuizQuestionMultipleViewModel
import com.ssoaharison.quiz.model.QuizQuestionMultipleViewModelFactory
import com.ssoaharison.quiz.model.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var appContext: Context? = null
    private lateinit var animFadeIn: Animation
    private lateinit var animFadeOut: Animation
    private var quizQuestionContainer: ConstraintLayout? = null

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
        quizQuestionContainer = getView()?.findViewById(R.id.clQuizQuestionContainer)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                quizViewModel.apply {
                    getQuizQuestionMultiple(10, 0, "", "multiple")
                    questionList.collect{
                        when (it) {
                            is UiState.Loading -> {
                                binding.cvLoading.isVisible = true
                            }
                            is UiState.Error -> {
                                binding.cvLoading.isVisible = false
                                Log.e(TAG, it.errorMessage)
                            }
                            is UiState.Success -> {
                                binding.cvLoading.isVisible = false
                                startQuiz(it.data)
                            }
                        }
                    }
                }
            }
        }


    }

    private fun startQuiz(questionList: List<Result>) {

        val _adapter = ViewPagerAdapter(questionList, appContext!!) {
            if (quizViewModel.isUserChoiceCorrect(it)) {
                binding.viewPager.apply {
                    beginFakeDrag()
                    fakeDragBy(-10f)
                    endFakeDrag()
                }
            } else {
                giveFeedback(it[2] as View, R.drawable.quiz_question_container_border_on_wrong)
            }
        }
        binding.viewPager.apply {
            adapter = _adapter
        }
    }

    private fun giveFeedback(viewToAnimate: View, color: Int) {
        viewToAnimate.startAnimation(animFadeOut)
        viewToAnimate.background = AppCompatResources.getDrawable(appContext!!, color)
        viewToAnimate.startAnimation(animFadeIn)
        lifecycleScope.launch {
            delay(500)
            viewToAnimate.background = AppCompatResources.getDrawable(appContext!!, R.color.green)
            viewToAnimate.startAnimation(animFadeIn)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}