package com.ssoaharison.quiz.quiz

import android.graphics.Color
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ssoaharison.quiz.R
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.backend.RetrofitClient
import com.ssoaharison.quiz.databinding.ActivityMainBinding
import com.ssoaharison.quiz.model.ExternalResult
import com.ssoaharison.quiz.model.SettingsModel
import com.ssoaharison.quiz.util.UiState
import kotlinx.coroutines.launch


class MainActivity :
    AppCompatActivity(),
    SettingsFragment.SettingsFragmentListener {

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

        window.statusBarColor = MaterialColors.getColor(
            this,
            com.google.android.material.R.attr.colorSurfaceContainerLowest,
            Color.BLACK
        )

        binding.viewPager.isUserInputEnabled = false
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        settingsModel = SettingsModel()
        getQuizAndStart()

        binding.btRestart.setOnClickListener {
            getQuizAndRestart()
        }

        binding.btSettings.setOnClickListener {
            showSettings()
        }

        binding.btNext.setOnClickListener {
            nextQuestion()
        }

//        binding.btPrevious.setOnClickListener {
//            previousQuestion()
//        }

    }

    private fun nextQuestion() {
        val itemPosition = binding.viewPager.currentItem
        if (itemPosition.plus(1) == quizViewModel.getQuestionSum()) {
            showReviewDialog(
                quizViewModel.getUserScore(),
                quizViewModel.getMissedSun(),
                quizViewModel.getMissedTime(),
                quizViewModel.getQuestionSum()
            )
        } else {
            binding.viewPager.setCurrentItem(itemPosition.plus(1), true)
            restoreAnswerButtons()
            areBackAndForwardButtonEnabled(false)
            quizViewModel.incrementRound()
            quizViewModel.initAttempt()
        }
    }

    private fun previousQuestion() {
        val itemPosition = binding.viewPager.currentItem
        if (itemPosition > 0) {
            restoreAnswerButtons()
            binding.viewPager.setCurrentItem(itemPosition.minus(1), true)
            areBackAndForwardButtonEnabled(false)
            quizViewModel.decrementRound()
        }
    }

    private fun restoreAnswerButtons() {
        binding.viewPager.findViewById<MaterialButton>(R.id.btAnswer1).apply {
            icon = null
            strokeWidth = 0
        }
        binding.viewPager.findViewById<MaterialButton>(R.id.btAnswer2).apply {
            icon = null
            strokeWidth = 0
        }
        binding.viewPager.findViewById<MaterialButton>(R.id.btAnswer3).apply {
            icon = null
            strokeWidth = 0
        }
        binding.viewPager.findViewById<MaterialButton>(R.id.btAnswer4).apply {
            icon = null
            strokeWidth = 0
        }
    }

    private fun showSettings() {
        val fragmentManager = supportFragmentManager
        val newDeckDialog = SettingsFragment()
        newDeckDialog.show(fragmentManager, "dialog settings")
    }

    private fun getQuizAndStart() {
        quizViewModel.initUserScore()
        quizViewModel.initRound()
        quizViewModel.initAttempt()
        quizQuestionContainer = findViewById(R.id.cl_quiz_uestion_container)
        binding.tvUserScore.text = getString(R.string.text_score, "${quizViewModel.getUserScore()}")
        setScore()
        quizViewModel.getQuizQuestionMultiple(
            settingsModel?.number!!,
            settingsModel?.category!!,
            settingsModel?.difficulty!!,
            settingsModel?.type!!
        )
        lifecycleScope.launch {
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
                        startQuiz(it.data)
                    }
                }
            }
        }
    }

    private fun getQuizAndRestart() {
        quizViewModel.initUserScore()
        quizViewModel.initRound()
        quizViewModel.initAttempt()
        quizQuestionContainer = findViewById(R.id.cl_quiz_uestion_container)
        binding.tvUserScore.text = getString(R.string.text_score, "${quizViewModel.getUserScore()}")
        setScore()
        lifecycleScope.launch {
            quizViewModel
                .questionList
                .collect {
                    when (it) {
                        is UiState.Loading -> {
                            binding.cvLoading.isVisible = true
                        }
                        is UiState.Error -> {
                            binding.cvLoading.isVisible = false
                            Log.e(TAG, it.errorMessage)
                        }
                        is UiState.Success -> {
                            startQuiz(it.data)
                        }
                    }
                }
        }
    }

    private fun startQuiz(it: List<ExternalResult>) {
        quizViewModel.initUserScore()
        quizViewModel.initRound()
        binding.cvLoading.isVisible = false
        viewPagerAdapter = ViewPagerAdapter(it, this@MainActivity) {
            if (binding.viewPager.currentItem < quizViewModel.getRound()) {
                showReviewDialog(
                    quizViewModel.getUserScore(),
                    quizViewModel.getMissedSun(),
                    quizViewModel.getMissedTime(),
                    quizViewModel.getQuestionSum()
                )
                //Snackbar.make(binding.root, "You finished the Quiz!", Snackbar.LENGTH_LONG).show()
                return@ViewPagerAdapter
            }
            if (quizViewModel.isUserChoiceCorrect(it)) {
                quizViewModel.increaseAttempt()
                quizViewModel.updateUserScore()
                quizViewModel.updateMissedTime()
//                binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
                setScore()
                giveFeedback(true, it.view)
                return@ViewPagerAdapter
            } else {
                quizViewModel.increaseAttempt()
                giveFeedback(false, it.view)
                return@ViewPagerAdapter
            }
        }
        binding.viewPager.adapter = viewPagerAdapter
    }

    private fun showReviewDialog(
        knownSum: Int,
        missedSum: Int,
        missedTime: Int,
        questionSum: Int
    ) {
        val message = "Known questions: $knownSum\n" +
                      "Missed questions: $missedSum\n" +
                      "Missed Time: $missedTime\n" +
                      "Question Sum: $questionSum\n"
        val builder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
        builder
            .setMessage(message)
            .setTitle("You finished the quiz with: ")
            .setPositiveButton("Restart") { dialog, which ->
                getQuizAndRestart()
                dialog.dismiss()
            }
            .setNegativeButton("Change settings") { dialog, which ->
                showSettings()
                dialog.dismiss()
            }

        builder.show()
    }



    private fun setScore() {
        binding.tvUserScore.text = getString(R.string.text_score, "${quizViewModel.getUserScore()}")
    }

    private fun onWrongAnswer(button: MaterialButton) {
        button.apply {
            icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_cancel_filled)
            iconTint = ContextCompat.getColorStateList(this@MainActivity, R.color.red700)
            iconSize = 70
            strokeWidth = 4
            strokeColor = ContextCompat.getColorStateList(this@MainActivity, R.color.red700)
        }
    }

    private fun onCorrectAnswer(button: MaterialButton) {
        button.apply {
            icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_check_circle_filled)
            iconTint = ContextCompat.getColorStateList(this@MainActivity, R.color.green)
            iconSize = 70
            strokeWidth = 4
            strokeColor = ContextCompat.getColorStateList(this@MainActivity, R.color.green)
        }
    }

    private fun resetButton(button: MaterialButton) {
        button.apply {
            icon = null
            strokeWidth = 0
        }
    }

    private fun giveFeedback(isUserAnswerCorrect: Boolean, button: MaterialButton) {
        if (isUserAnswerCorrect) {
            onCorrectAnswer(button)
        } else {
            onWrongAnswer(button)
        }
        areBackAndForwardButtonEnabled(isUserAnswerCorrect)
    }

    private fun areBackAndForwardButtonEnabled(areEnabled: Boolean) {
        binding.btNext.apply {
            isVisible = areEnabled
            isEnabled = areEnabled
        }
//        binding.btPrevious.apply {
//            isVisible = areEnabled
//            isEnabled = areEnabled
//        }
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    override fun onSettingsDialogPositiveClick(settings: SettingsModel) {
        settingsModel = settings
        getQuizAndStart()
    }
}