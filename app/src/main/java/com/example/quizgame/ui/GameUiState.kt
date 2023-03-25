package com.example.quizgametest.ui

import com.example.quizgame.data.Question

data class GameUiState(
    val currentQuestionCount: Int = 1,
    val currentQuestion: Question,
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val selectedAnswer: String = "",
    val result: String = "",
    )