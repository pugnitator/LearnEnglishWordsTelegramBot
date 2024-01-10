package org.example.dictionary

data class Word(
    val original: String,
    val translation: String,
    val numberOfCorrectAnswers: Int? = 0
)