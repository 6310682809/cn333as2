package com.example.quizgame.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.quizgame.R
import com.example.quizgame.data.Question
import com.example.quizgametest.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    var currentQuestion by remember { mutableStateOf(viewModel.getNextQuestion()) }
    var selectedAnswer by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val quizUiState by viewModel.uiState.collectAsState()

    if (currentQuestion == null) {
        currentQuestion = viewModel.getNextQuestion()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameStatus(
            questionCount = quizUiState.currentQuestionCount,
            score = quizUiState.score
        )
        currentQuestion?.let {
            GameLayout(
                currentQuestion = it,
            )
        }

        currentQuestion!!.options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedAnswer == option,
                    onClick = { selectedAnswer = option },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colors.primary
                    )
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (result.isNotBlank()) {
            Text(
                text = result,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        if (quizUiState.isGameOver ) {

            FinalScoreDialog(
                score = quizUiState.score,
                onPlayAgain = {
                    viewModel.reset()
                    currentQuestion = viewModel.getNextQuestion()
                    selectedAnswer = ""
                    result = ""
                }
            )

        } else {
            Button(
                onClick = {
                    if(selectedAnswer == ""){
                        result = "Please select an option."
                    }
                    else{
                        val isCorrect = viewModel.checkAnswer(selectedAnswer)
                        result = if (isCorrect) {
                            "Correct!"
                        } else {
                            "Incorrect! The correct answer was ${currentQuestion!!.correctAnswer}."
                        }
                        currentQuestion = viewModel.getNextQuestion()
                        selectedAnswer = ""
                    }

                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Submit Answer")
            }

        }
    }
}

@Composable
fun GameStatus(questionCount: Int, score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .size(48.dp),
    ) {
        Text(
            text = stringResource(R.string.question_count, questionCount),
            fontSize = 18.sp,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            text = stringResource(R.string.score, score),
            fontSize = 18.sp,
        )
    }
}

@Composable
fun GameLayout(
    currentQuestion: Question,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Math Quiz",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = currentQuestion!!.question,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

    }
}
@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(stringResource(R.string.congratulations)) },
        text = { Text(stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}
