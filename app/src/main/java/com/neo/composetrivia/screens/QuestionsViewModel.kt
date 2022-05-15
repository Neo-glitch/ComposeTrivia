package com.neo.composetrivia.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neo.composetrivia.data.DataOrException
import com.neo.composetrivia.model.QuestionItem
import com.neo.composetrivia.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QuestionsViewModel @Inject constructor(private val repository: QuestionRepository): ViewModel() {

    // Mutable state init as having no data and loading = true and exception is empty string
    val data: MutableState<DataOrException<ArrayList<QuestionItem>, Boolean, Exception>> =
        mutableStateOf(DataOrException(null, true, Exception("")))

    init {
        getAllQuestions()
    }

    private fun getAllQuestions(){
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllQuestions()

            if(data.value.data.toString().isNotEmpty()){
                data.value.loading = false
            }
        }
    }

    fun getTotalQuestionsCount(): Int = data.value.data?.toMutableList()?.size!!

}