package com.ssoaharison.quiz.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
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
import com.ssoaharison.quiz.model.SettingsModel
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
    private var settingsModel: SettingsModel? = null

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

        settingsModel = SettingsModel(10, 0, "", "")
        getQuizQuestions(settingsModel!!)
        startQuiz()

        binding.btRestart.setOnClickListener {
            startQuiz()
        }

        binding.btSettings.setOnClickListener {
            val newDeckDialog = SettingsFragment()
            newDeckDialog.show(parentFragmentManager, "Settings Dialog")

            setFragmentResultListener("requestKey") { _, bundle ->
                val result = bundle.parcelable<SettingsModel>("requestKey")
                result?.let {
                    settingsModel = it
                    getQuizQuestions(it)
                    startQuiz()
                }
            }
        }
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("viewPagerPosition", binding.viewPager.currentItem)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.viewPager.currentItem = savedInstanceState.getInt("viewPagerPosition")
        }
    }


    private fun getQuizQuestions(settingsModel: SettingsModel) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                quizViewModel.getQuizQuestionMultiple(settingsModel.number!!, settingsModel.category!!, settingsModel.difficulty!!, settingsModel.type!!)
            }
        }
    }

    private fun startQuiz(){
        quizViewModel.initUserScore()
        quizViewModel.initRound()
        quizQuestionContainer = getView()?.findViewById(R.id.cl_quiz_uestion_container)
        binding.tvUserScore.text = getString(R.string.text_score, "${quizViewModel.getUserScore()}")
        setScore()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                quizViewModel.questionList.collect {
                    when (it) {
                        is UiState.Loading -> {
                            binding.cvLoading.isVisible = true
                        }

                        is UiState.Error -> {
                            binding.cvLoading.isVisible = false
                            Log.e(TAG, it.errorMessage)
                        }

                        is UiState.Success -> {
                            quizViewModel.initUserScore()
                            quizViewModel.initRound()
                            binding.cvLoading.isVisible = false
                            val _adapter = ViewPagerAdapter(it.data, appContext!!) {
                                if ( binding.viewPager.currentItem < quizViewModel.getRound()) {
                                    Snackbar.make(binding.root, "You finished the Quiz!", Snackbar.LENGTH_LONG).show()
                                    return@ViewPagerAdapter
                                }
                                if (quizViewModel.isUserChoiceCorrect(it)) {
                                    quizViewModel.incrementUserScore()
                                    quizViewModel.incrementRound()
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
                            return@collect
                        }
                    }
                }
            }
        }
    }

    private fun setScore() {
        binding.tvUserScore.text = getString(R.string.text_score, "${quizViewModel.getUserScore()}")
    }

    private fun giveFeedback(viewToAnimate: View, color: Int) {
        viewToAnimate.backgroundTintList = ContextCompat.getColorStateList(appContext!!, R.color.red700)
        viewToAnimate.startAnimation(animFadeIn)
        lifecycleScope.launch {
            delay(700)
            viewToAnimate.backgroundTintList = ContextCompat.getColorStateList(appContext!!, color)
            viewToAnimate.startAnimation(animFadeIn)
        }
    }
}