package com.ssoaharison.quiz.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.model.ExternalResult
import com.ssoaharison.quiz.model.UserAnswerModel
import com.ssoaharison.quiz.util.EXAMPLE_QUESTIONS
import com.ssoaharison.quiz.util.UiState
import com.ssoaharison.quiz.util.toExternal
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class QuizQuestionMultipleViewModel constructor(
    private val quizRepository: QuizRepository
): ViewModel() {

    private var _questionList = MutableStateFlow<UiState<List<ExternalResult>>>(UiState.Loading)
    val questionList: StateFlow<UiState<List<ExternalResult>>> = _questionList.asStateFlow()
    var job: Job? = null
    private var userScore = 0
    private var round = 0
    private var attempt = 0
    private var missedTime = 0
    private var questionSum = 0

    fun getQuizQuestionMultiple(amount: Int, category: Int, difficulty: String, type: String) {
        job?.cancel()
        job = viewModelScope.launch {
            try {
                // Switch to this response on App Demo or Release
                val response = quizRepository.getQuizQuestionMultiple("$amount", setCategory(category), difficulty, type)
//                val response = null
                _questionList.value = UiState.Success(response ?: EXAMPLE_QUESTIONS.toExternal())
            } catch (e:IOException) {
                _questionList.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun setQuestionSum(sum: Int) {
        questionSum = sum
    }

    fun isUserChoiceCorrect(choice: UserAnswerModel) = choice.correctAnswer == choice.userAnswer

    fun updateMissedTime() {
        missedTime += attempt.minus(1)
    }

    fun iniMissedTime() {
        missedTime = 0
    }

    fun getMissedTime() = missedTime

    fun getMissedSun() = questionSum.minus(userScore)

    fun getQuestionSum() = questionSum

    private fun incrementUserScore() {
        userScore += 1
    }

    fun updateUserScore() {
        if (attempt <= 1) {
            incrementUserScore()
        }
    }

    fun incrementRound() {
        round++
    }
    fun decrementRound() {
        if (round > 0) {
            round--
        }
    }

    fun increaseAttempt() {
        attempt++
    }

    fun initAttempt() {
        attempt = 0
    }

    fun getRound() = round

    fun getUserScore() = userScore

    fun initUserScore() {
        userScore = 0
    }

    fun initRound() {
        round = 0
    }

    private fun setCategory(category: Int): String {
        if (category > 0) {
            return "$category"
        }
        return ""
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}

class QuizQuestionMultipleViewModelFactory constructor(private val repository: QuizRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(QuizQuestionMultipleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            QuizQuestionMultipleViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}