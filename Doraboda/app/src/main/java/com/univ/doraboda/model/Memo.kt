package com.univ.doraboda.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "memoTable")
data class Memo (
    @PrimaryKey var ID: Date,
    var memo: String
)