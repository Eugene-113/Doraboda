package com.univ.doraboda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memoTable")
data class Memo (
    @PrimaryKey var ID: String = "",
    var memo: String
)