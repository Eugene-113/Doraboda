package com.univ.doraboda.calendar.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.univ.doraboda.settings.model.JEmotion
import java.util.Date

@Entity(tableName = "emotionTable")
data class Emotion (
    @PrimaryKey var ID: Date,
    var emotion: String
)

fun Emotion.toJEmotion() = JEmotion(ID.time, emotion)
fun List<Emotion>.toJEmotionList() = map{ it.toJEmotion() }