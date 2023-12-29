package com.ssoaharison.quiz.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.model.Result
import com.ssoaharison.quiz.util.UiState
import com.ssoaharison.quiz.util.EXAMPLE_QUESTIONS
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class QuizQuestionMultipleViewModel constructor(
    private val quizRepository: QuizRepository
): ViewModel() {

    private var _questionList = MutableStateFlow<UiState<List<Result>>>(UiState.Loading)
    val questionList: StateFlow<UiState<List<Result>>> = _questionList.asStateFlow()
    var job: Job? = null
    val loading = MutableLiveData<Boolean>()
    private var userScore = 0
    private var round = 0

    fun getQuizQuestionMultiple(amount: Int, category: Int, difficulty: String, type: String) {
        job?.cancel()
        job = viewModelScope.launch {
            try {
                val response = quizRepository.getQuizQuestionMultiple("$amount", setCategory(category), difficulty, type)
                _questionList.value = UiState.Success(response.body()?.results!!)
                //val response = EXAMPLE_QUESTIONS
                //_questionList.value = UiState.Success(response)
            } catch (e:IOException) {
                _questionList.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun isUserChoiceCorrect(choice: List<Any>) = choice[0] == choice[1]

    fun incrementUserScore() {
        userScore += 1
    }

    fun incrementRound() {
        round += 1
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