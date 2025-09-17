package com.univ.doraboda.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "emotionTable")
data class Emotion (
    @PrimaryKey var ID: Date,
    var emotion: String
)