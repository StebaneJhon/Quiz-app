package com.ssoaharison.quiz.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.quiz.UiState
import com.ssoaharison.quiz.util.EXAMPLE_QUESTIONS
import kotlinx.coroutines.CoroutineExceptionHandler
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

    fun getQuizQuestionMultiple(amount: Int, category: Int, difficulty: String, type: String) {
        job?.cancel()
        job = viewModelScope.launch {
            /*
            val response = quizRepository.getQuizQuestionMultiple("$amount", setCategory(category), difficulty, type)
            if (response.isSuccessful) {
                questionList.postValue(response.body())
                loading.value = false
            } else {
                onError("Error: ${response.message()}")
            }

             */
            try {
                _questionList.value = UiState.Success(EXAMPLE_QUESTIONS)
            } catch (e:IOException) {
                _questionList.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun isUserChoiceCorrect(choice: List<Any>) = choice[0] == choice[1]

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