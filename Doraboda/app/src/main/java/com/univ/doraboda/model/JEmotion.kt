package com.univ.doraboda.model

import java.util.Date

data class JEmotion (
    var ID: Long,
    var emotion: String
)

fun JEmotion.toEmotion() = Emotion(Date(ID), emotion)
fun List<JEmotion>.toEmotionList() = map { it.toEmotion() }