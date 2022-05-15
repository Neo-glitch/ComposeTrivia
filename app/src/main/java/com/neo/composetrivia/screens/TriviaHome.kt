package com.neo.composetrivia.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.neo.composetrivia.component.Questions


@ExperimentalFoundationApi
@Composable
fun TriviaHome(viewModel: QuestionsViewModel = hiltViewModel()){
    Questions(viewModel)
}


