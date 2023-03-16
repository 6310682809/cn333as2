package com.example.quizgame.data

data class Question(
    val question: String,
    var options: List<String>,
    val correctAnswer: String
)