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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.ssoaharison.quiz.QuizCallback
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.backend.RetrofitClient
import com.ssoaharison.quiz.databinding.FragmentQuizBinding
import com.ssoaharison.quiz.model.Result
import com.ssoaharison.quiz.model.SettingsModel
import com.ssoaharison.quiz.util.QUESTION_FONT_TINTS
import com.ssoaharison.quiz.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizFragment : Fragment(), SettingsFragment.NewDialogListener {

    private var _binding: FragmentQuizBinding? = null

    private var callback: QuizCallback? = null
    private val binding get() = _binding!!
    private var appContext: Context? = null
    private lateinit var animFadeIn: Animation
    private lateinit var animFadeOut: Animation
    private var quizQuestionContainer: ConstraintLayout? = null
    private var userScore: Int = 0
    private var round: Int = 0
    private lateinit var settingsModel: SettingsModel

    fun setCallback(callback: QuizCallback) {
        this.callback = callback
        settingsModel = callback.getSettingsModel()
    }

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

        launchQuiz(settingsModel)

        binding.btRestart.setOnClickListener {
            launchQuiz(settingsModel)
        }

        binding.btSettings.setOnClickListener {
            val newDeckDialog = SettingsFragment()
            newDeckDialog.show(parentFragmentManager, "Settings Dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun launchQuiz(settingsModel: SettingsModel) {
        userScore = 0
        round = 0
        quizQuestionContainer = getView()?.findViewById(R.id.cl_quiz_uestion_container)
        binding.tvUserScore.text = getString(R.string.text_score, "$userScore")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                quizViewModel.apply {
                    getQuizQuestionMultiple(settingsModel.number!!, settingsModel.category!!, settingsModel.difficulty!!, settingsModel.type!!)
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
        setScore()
        val _adapter = ViewPagerAdapter(questionList, appContext!!) {
            if ( binding.viewPager.currentItem < round) {
                Snackbar.make(binding.root, "You finished the Quiz!", Snackbar.LENGTH_LONG).show()
                return@ViewPagerAdapter
            }
            if (quizViewModel.isUserChoiceCorrect(it)) {
                    userScore += 1
                    round += 1
                    binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
                    setScore()
                    return@ViewPagerAdapter
            } else {
                giveFeedback(it[2] as View, it[4] as Int)
                return@ViewPagerAdapter
            }
        }
        binding.viewPager.apply {
            adapter = _adapter
        }
        return
    }

    private fun setScore() {
        binding.tvUserScore.text = getString(R.string.text_score, "$userScore")
    }

    private fun giveFeedback(viewToAnimate: View, color: Int) {
        viewToAnimate.startAnimation(animFadeIn)
        //viewToAnimate.background = AppCompatResources.getDrawable(appContext!!, color)
        viewToAnimate.backgroundTintList = ContextCompat.getColorStateList(appContext!!, R.color.red700)
        lifecycleScope.launch {
            delay(700)
            viewToAnimate.backgroundTintList = ContextCompat.getColorStateList(appContext!!, color)
            viewToAnimate.startAnimation(animFadeIn)
        }
    }

    override fun getSettingsModel(settingsModel: SettingsModel) {
        launchQuiz(settingsModel)
    }
}