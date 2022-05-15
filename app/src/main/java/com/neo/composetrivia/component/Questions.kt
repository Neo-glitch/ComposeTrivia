package com.neo.composetrivia.component

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neo.composetrivia.model.QuestionItem
import com.neo.composetrivia.screens.QuestionsViewModel
import com.neo.composetrivia.util.AppColors

@ExperimentalFoundationApi
@Composable
fun Questions(viewModel: QuestionsViewModel) {

    val questions =
        viewModel.data.value.data?.toMutableList()  // since composable work with mutable list"

    // index of current question being answered
    val questionIndex = remember { mutableStateOf(0) }

    if (viewModel.data.value.loading == true) {
        CircularProgressIndicator()
        Log.d("Loading", "Questions ... Loading")
    } else {
        val question = try {
            questions?.get(questionIndex.value)
        } catch (e: Exception) {
            null
        }

        if (questions != null) {
            QuestionDisplay(
                question = question!!,
                questionIndex = questionIndex,
                viewModel = viewModel,
                onNextClicked = {
                    // increase the index and take us to next page
                    questionIndex.value = questionIndex.value + 1
                }
            ) {
                // decreases the index and take us to previous page
                questionIndex.value = questionIndex.value - 1
            }
        }
        Log.d("SIZE", "Questions: ${questions?.size}")
    }
}


//@Preview
@ExperimentalFoundationApi
@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClicked: (Int) -> Unit = {},
    onPreviousClicked: (Int) -> Unit = {}
) {
    // what is being monitored is question.choices
    val choicesState = remember(question) {
        question.choices.toMutableList()
    }

    // our chosen answer state
    val answerState = remember(question) { mutableStateOf<Int?>(null) }
    val correctAnswerState = remember(question) { mutableStateOf<Boolean?>(null) }

    // remember with lambda(new)(to update our choice and check if answer is correct)
    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            // if true for choice then answer is correct
            correctAnswerState.value = choicesState[it] == question.answer
        }
    }
    // creates dash of 10 pixels and dash space of 10 pixels
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    val totalQuestionsCount = viewModel.getTotalQuestionsCount()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = AppColors.mDarkPurple
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            if (questionIndex.value >= 3) // show progress only after answering three questions
                showProgress(score = questionIndex.value)
            QuestionTracker(
                counter = questionIndex.value,
                outOf = totalQuestionsCount
            )
            DrawDottedLine(pathEffect = pathEffect)

            Column {
                Text(
                    text = question.question,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                        .fillMaxHeight(0.3f),
                    fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                    color = AppColors.mOffWhite
                )

                // choices or options
                choicesState.forEachIndexed { index, answerText ->
                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxWidth()
                            .height(45.dp)
                            .border(
                                width = 4.dp,
                                brush = Brush.linearGradient(
                                    // first color is at start and last color at end
                                    colors = listOf(
                                        AppColors.mOffDarkPurple,
                                        AppColors.mOffDarkPurple
                                    )
                                ), shape = RoundedCornerShape(15.dp)
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStartPercent = 50, topEndPercent = 50,
                                    bottomEndPercent = 50, bottomStartPercent = 50
                                )
                            )
                            .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (answerState.value == index),  // if choosen, set as true
                            onClick = {
                                updateAnswer(index)
                            },
                            modifier = Modifier.padding(start = 16.dp),
                            colors = RadioButtonDefaults
                                .colors(
                                    selectedColor =
                                    if (correctAnswerState.value == true
                                        && index == answerState.value
                                    ) {
                                        Color.Green.copy(0.2f)
                                    } else {
                                        Color.Red.copy(0.2f)
                                    }
                                )
                        )  // end of Radio Btn

                        val annotatedString = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Light,
                                    // changes color only if an answer is suggested else if no answer
                                    // selected stays as default
                                    color = if (correctAnswerState.value == true
                                        && index == answerState.value
                                    ) {
                                        Color.Green
                                    } else if (correctAnswerState.value == false
                                        && index == answerState.value
                                    ) {
                                        Color.Red
                                    } else {
                                        AppColors.mOffWhite
                                    },
                                    fontSize = 17.sp
                                )
                            ) {
                                append(answerText)
                            }
                        }
                        Text(text = annotatedString, modifier = Modifier.padding(6.dp))
                    }
                }
                Button(
                    onClick = {
                        if (questionIndex.value != totalQuestionsCount)
                            onNextClicked(questionIndex.value)
                    },
                    modifier = Modifier
                        .padding(3.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.mLightBlue
                    )
                ) {
                    Text(
                        text = "Next",
                        modifier = Modifier.padding(4.dp),
                        color = AppColors.mOffWhite, fontSize = 17.sp
                    )
                }

                Button(
                    onClick = {
                        if (questionIndex.value != 0)
                            onPreviousClicked(questionIndex.value)
                    },
                    modifier = Modifier
                        .padding(vertical = 6.dp, horizontal = 3.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.mLightBlue
                    )
                ) {
                    Text(
                        text = "Back",
                        modifier = Modifier.padding(4.dp),
                        color = AppColors.mOffWhite, fontSize = 17.sp
                    )
                }
            }

        }
    }
}


/**
 * for drawing dotted line using [pathEffect]
 */
@ExperimentalFoundationApi
@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        contentDescription = "dotted line"
    ) {
        drawLine(
            color = AppColors.mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}

@Preview
@Composable
fun QuestionTracker(counter: Int = 10, outOf: Int = 100) {

    Text(
        text = buildAnnotatedString {
            withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {
                // paragraph without indent
                withStyle(
                    style = SpanStyle(
                        color = AppColors.mLightGray,
                        fontWeight = FontWeight.Bold, fontSize = 27.sp
                    )
                ) {
                    append("Question $counter/")
                    withStyle(
                        style = SpanStyle(
                            color = AppColors.mLightGray,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    ) {
                        append("$outOf")
                    }
                }
            }
        },
        modifier = Modifier.padding(20.dp)
    )

}


/**
 * ProgressBar to track number of questions answered
 */
@Preview
@Composable
fun showProgress(score: Int = 12) {

    // gradient to be used in Progress
    val progressGradient = Brush.linearGradient(
        listOf(
            Color(0xFFF95075),
            Color(0xFFBE6BE5)
        )
    )

    var progressFactor = remember(score) {
        mutableStateOf(score * 0.005f)
    }

    Row(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AppColors.mLightBlue, AppColors.mLightPurple
                    )
                ),
                shape = RoundedCornerShape(34.dp)
            )
            .clip(
                RoundedCornerShape(
                    topStartPercent = 50, topEndPercent = 50,
                    bottomStartPercent = 50, bottomEndPercent = 50
                )
            )
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {},
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier
                .fillMaxWidth(progressFactor.value)
                .background(brush = progressGradient),
            enabled = false,
            elevation = null,
            // to make button color not to change when clicked or disabled
            colors = buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent
            )
        ) {
            Text(
                text = (score).toString(),
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(0.87f)
                    .fillMaxWidth()
                    .padding(6.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center
            )
        }
    }


}