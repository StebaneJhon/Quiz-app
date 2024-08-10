package com.ssoaharison.quiz.quiz

import android.graphics.Color
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.backend.QuizApplication
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.backend.RetrofitClient
import com.ssoaharison.quiz.databinding.ActivityMainBinding
import com.ssoaharison.quiz.model.Result
import com.ssoaharison.quiz.model.SettingsModel
import com.ssoaharison.quiz.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity :
    AppCompatActivity(),
    SettingsFragment.SettingsFragmentListener
{

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel by lazy {
        val repository = QuizRepository(RetrofitClient)
        ViewModelProvider(
            this,
            QuizQuestionMultipleViewModelFactory(repository)
        )[QuizQuestionMultipleViewModel::class.java]
    }

    private lateinit var animFadeIn: Animation
    private lateinit var animFadeOut: Animation
    private var quizQuestionContainer: ConstraintLayout? = null
    private var settingsModel: SettingsModel? = null

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.statusBarColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceContainerLowest, Color.BLACK)

        binding.viewPager.isUserInputEnabled = false
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        settingsModel = SettingsModel(10, 0, "", "")
        getQuizQuestions(settingsModel!!)
        startQuiz()

        binding.btRestart.setOnClickListener {
            startQuiz()
        }

        binding.btSettings.setOnClickListener {
            showSettings()

        }

    }

    private fun showSettings() {
        val fragmentManager = supportFragmentManager
        val newDeckDialog = SettingsFragment()
        newDeckDialog.show(fragmentManager, "dialog settings")
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
        quizQuestionContainer = findViewById(R.id.cl_quiz_uestion_container)
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
                            lunchQuiz(it)
                        }
                    }
                }
            }
        }
    }

    private fun lunchQuiz(it: UiState.Success<List<Result>>) {
        quizViewModel.initUserScore()
        quizViewModel.initRound()
        binding.cvLoading.isVisible = false
        viewPagerAdapter = ViewPagerAdapter(it.data, this@MainActivity) {
            if (binding.viewPager.currentItem < quizViewModel.getRound()) {
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
        binding.viewPager.adapter = viewPagerAdapter
    }

    private fun setScore() {
        binding.tvUserScore.text = getString(R.string.text_score, "${quizViewModel.getUserScore()}")
    }

    private fun giveFeedback(viewToAnimate: View, color: Int) {
        viewToAnimate.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red700)
        viewToAnimate.startAnimation(animFadeIn)
        lifecycleScope.launch {
            delay(700)
            viewToAnimate.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, color)
            viewToAnimate.startAnimation(animFadeIn)
        }
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    override fun onSettingsDialogPositiveClick(settings: SettingsModel) {
        settingsModel = settings
        getQuizQuestions(settings)
        startQuiz()
    }
}