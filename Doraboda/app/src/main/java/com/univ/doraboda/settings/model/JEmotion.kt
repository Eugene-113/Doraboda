package com.univ.doraboda.settings.model

import com.univ.doraboda.calendar.db.model.Emotion
import java.util.Date

data class JEmotion (
    var ID: Long,
    var emotion: String
)

fun JEmotion.toEmotion() = Emotion(Date(ID), emotion)
fun List<JEmotion>.toEmotionList() = map { it.toEmotion() }