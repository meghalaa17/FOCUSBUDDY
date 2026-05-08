package com.example.a211393_nelson_lab01.data
//blueprint template that defines the shape of viewmodel data
//only three shared data cause multiple fields

data class StudySlot(
    val id: Int,
    val subject: String,
    val day: String,
    val time: String,
    val duration: String
)

data class Flashcard(
    val id: Int,
    val question: String,
    val answer: String
)

data class StudySession(
    val id: Int,
    val type: String,
    val durationMinutes: Int,
    val xpEarned: Int,
    val date: String = "Today"
)