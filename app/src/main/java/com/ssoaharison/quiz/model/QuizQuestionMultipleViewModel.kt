package com.ssoaharison.quiz.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ssoaharison.quiz.backend.QuizRepository
import com.ssoaharison.quiz.util.EXAMPLE_QUESTIONS
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class QuizQuestionMultipleViewModel constructor(
    private val quizRepository: QuizRepository
): ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val questionList = MutableLiveData<List<Result>>()
    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun getQuizQuestionMultiple(amount: Int, category: Int, difficulty: String, type: String) {
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
            questionList.postValue(EXAMPLE_QUESTIONS)
        }
    }

    private fun setCategory(category: Int): String {
        if (category > 0) {
            return "$category"
        }
        return ""
    }


    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
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