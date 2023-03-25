package com.example.quizgametest

import androidx.lifecycle.ViewModel
import com.example.quizgame.data.Question
import com.example.quizgame.data.questions
import com.example.quizgametest.ui.GameUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private var questionIndex = 0
    private val usedQuestions = mutableListOf<Int>()

    private val _uiState = MutableStateFlow(GameUiState(
        currentQuestion = getNextQuestion(),
    ))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun getNextQuestion(): Question {
        if (usedQuestions.size == questions.size) {
            return questions[questionIndex]
        }

        var index = (0 until questions.size).random()
        while (usedQuestions.contains(index)) {
            index = (0 until questions.size).random()
        }
        usedQuestions.add(index)
        questionIndex = index
        questions[index].options = questions[index].options.shuffled()
        return questions[index]
    }

    fun checkAnswer(answer: String): Boolean {
        val correct = answer == _uiState.value.currentQuestion.correctAnswer
        if (usedQuestions.size == 10) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGameOver = true
                )
            }
        }
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    currentQuestionCount = currentState.currentQuestionCount.inc()
                )
            }
        }
        if (correct) {
            _uiState.update { currentState ->
                currentState.copy(
                    score = currentState.score.inc()
                )
            }
        }
        return correct
    }

    fun checkselectedAnswer() {
        if(_uiState.value.selectedAnswer == ""){
            _uiState.update { currentState ->
                currentState.copy(
                    result = "Please select an option."
                )
            }
        }
        else{
            val isCorrect = checkAnswer(_uiState.value.selectedAnswer)
            val result = if (isCorrect) {
                "Correct!"
            } else {
                "Incorrect! The correct answer was ${_uiState.value.currentQuestion.correctAnswer}."
            }
            _uiState.update { currentState ->
                currentState.copy(
                    result = result,
                    selectedAnswer = "",
                    currentQuestion = getNextQuestion()
                )
            }
        }
    }

    fun setselectedAnswer(option: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedAnswer = option
            )
        }
    }

    fun reset() {
        questionIndex = 0
        usedQuestions.clear()
        _uiState.value = GameUiState(
            isGameOver = false,
            currentQuestion = getNextQuestion(),
            selectedAnswer = "",
            result = ""
        )
    }
}